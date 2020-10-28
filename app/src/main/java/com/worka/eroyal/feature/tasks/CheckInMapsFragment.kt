package com.worka.eroyal.feature.tasks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager.beginDelayedTransition
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.bus.FakeGps
import com.worka.eroyal.databinding.FragmentCheckInMapsBinding
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.getLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-29.
 */
class CheckInMapsFragment : BaseFragment(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener {

    private val TAG = CheckInMapsFragment::class.java.simpleName

    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentCheckInMapsBinding
    private var mMap: GoogleMap? = null
    private var slideTransition : Transition? = null

    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var isStartRequested: Boolean = false
    private var locationSettingsRequest: LocationSettingsRequest? = null

    private val fusedLocationClient by lazy {
        context?.let {ctx ->
            LocationServices.getFusedLocationProviderClient(ctx)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_in_maps, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.viewModel = viewModel

        GlobalScope.launch(Dispatchers.Main) {
            initGetLocation()
        }

        slideTransition = Slide(Gravity.BOTTOM).apply {
            duration = 300
            addTarget(binding.tvMessageCheckin)
            addTarget(binding.btnCheckIn)
        }
        getLocation {
            viewModel.checkCustomerDistance()
        }

        binding.btnCheckIn.setOnClickListener {
            mActivity.showLoading()
            getLocation {
                with(viewModel) {
                    if (checkCustomerDistance()) {
                        checkIn({
                            mActivity.hideLoading()
                            navController.navigate(R.id.action_checkInMapsFragment_to_signatureFormFragment)
                        }, {
                            mActivity.hideLoading()
                            context?.let { ctx -> CustomInfoDialog(ctx, it) }
                        })
                    } else {
                        mActivity.hideLoading()
                        context?.let { ctx ->
                            CustomInfoDialog(
                                ctx,
                                context?.getString(R.string.make_sure_location)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        googleApiClient?.let {
            if (it.isConnected) {
                it.disconnect()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {
                setMapPadding()
                if (viewModel.checkCustomerDistance()) {
                    beginDelayedTransition(view as ViewGroup, slideTransition)
                    binding.tvMessageCheckin.visibility = View.GONE
                } else {
                    beginDelayedTransition(view as ViewGroup, slideTransition)
                    binding.tvMessageCheckin.visibility = View.VISIBLE
                }
            }
        )

        setGetDirectionsButton()
    }

    private fun initGetLocation() {
        mActivity.requestForPermissions(
            Constants.LOCATION_PERMISSIONS,
            Constants.REQUEST_LOCATION,
            grantResults = { _, permissionResult ->
                if (permissionResult == PermissionResult.GRANTED) {
                    context?.let { ctx ->
                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                            return@let
                        }
                        createGoogleApiClient()
                        createLocationRequest()
                        if (!googleApiClient?.isConnected!!) {
                            googleApiClient?.connect()
                        }
                    }
                }
            })
    }

    private fun createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 1000
        locationRequest?.fastestInterval = 500

        locationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(it)
            builder.setAlwaysShow(true)
            locationSettingsRequest = builder.build()

            checkLocationSettings()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        setMapPadding()
        val builder = LatLngBounds.Builder()
        viewModel.selectedCustomer.get()?.getLocation()?.let {
            val latLngCustomer = LatLng(it.latitude, it.longitude)
            builder.include(latLngCustomer)
            googleMap.addMarker(MarkerOptions().position(latLngCustomer).title(viewModel.selectedCustomer.get()?.name))
        }
        viewModel.currentLocation.value?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            builder.include(latLng)
            val bounds = builder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 25)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    private fun getLocation(cbOnSuccess:() -> Unit) {
        mActivity.requestForPermissions(Constants.LOCATION_PERMISSIONS, Constants.REQUEST_LOCATION, grantResults = { _, permissionResult ->
            if (permissionResult == PermissionResult.GRANTED) {
                context?.let { ctx ->
                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return@let
                    }
                    fusedLocationClient?.lastLocation?.apply {
                        addOnSuccessListener { location ->
                            location?.let {
                                if (!location.isFromMockProvider) {
                                    onUpdateLocation(LocationData(location.latitude, location.longitude))
                                    Handler().postDelayed({ cbOnSuccess.invoke() }, 500)
                                } else {
                                    mActivity.hideLoading()
                                    mActivity.showFakeGpsDialog(FakeGps(true))
                                }
                            } ?: run {
                                mActivity.hideLoading()
                                CustomInfoDialog(ctx, ctx.getString(R.string.failed_fetch_your_location), withoutCancel = true)
                            }
                        }
                        addOnFailureListener {
                            mActivity.hideLoading()
                            CustomInfoDialog(ctx, ctx.getString(R.string.failed_fetch_your_location), withoutCancel = true)
                        }
                    }
                }
            }
        })
    }

    private fun checkLocationSettings() {
        val result = LocationServices.SettingsApi
            .checkLocationSettings(googleApiClient, locationSettingsRequest)
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    startLocationUpdates()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> { }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
            }
        }
    }

    private fun startLocationUpdates() {
        context?.let { ctx ->
            if (googleApiClient?.isConnected!! && !isStartRequested) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                    return
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this
                )
                isStartRequested = true
            }
        }
    }

    fun onUpdateLocation(location: LocationData){
        Log.e("Location", "latitude: ${location.latitude} longitude: ${location.longitude}")
        if (viewModel.currentLocation.value == null){
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            mMap?.moveCamera(cameraUpdate)
        }

        viewModel.currentLocation.value = location
    }

    private fun goBack() {
        viewModel.navController.popBackStack()
    }

    private fun setMapPadding(){
        mMap?.setPadding(0, 0, 0, binding.cvCheckIn.measuredHeight + 68)
    }

    private fun setGetDirectionsButton(){
        val content = SpannableString(context?.getString(R.string.get_direction))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.btnGetDirections.text = content

        binding.btnGetDirections.setOnClickListener {
            val taskLocation = viewModel.selectedCustomer.get()?.getLocation()
            val uri = "http://maps.google.com/maps?q=loc:" + taskLocation?.latitude + "," + taskLocation?.longitude + " (" + viewModel.selectedCustomer.get()?.name + ")"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            context?.startActivity(intent)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, connectionResult.toString())
    }

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient?.connect()
    }

    override fun onLocationChanged(loc: Location?) {
        loc?.let {
            if (!it.isFromMockProvider) {
                onUpdateLocation(LocationData(it.latitude, it.longitude))
            } else {
                if (!mActivity.isDestroyed) {
                    mActivity.showFakeGpsDialog(FakeGps(true))
                }
            }
        }
    }
}
