package com.worka.eroyal.feature.tasks.visit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.bus.FakeGps
import com.worka.eroyal.databinding.FragmentFollowUpVisitBinding
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.tasks.CustomerItemViewModel
import com.worka.eroyal.feature.tasks.TasksViewModel
import com.worka.eroyal.utils.ImagePickerHelper
import com.worka.eroyal.utils.TextWatcherHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource


class FollowUpVisitFragment : BaseFragment(), GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener {
    private val TAG = FollowUpVisitFragment::class.java.simpleName

    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentFollowUpVisitBinding
    private var adapter: GenericAppAdapter<CustomerItemViewModel>? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val easyImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var isStartRequested: Boolean = false
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private val fusedLocationClient by lazy {
        context?.let {ctx ->
            LocationServices.getFusedLocationProviderClient(ctx)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_follow_up_visit, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        initSearch()
        setupPlaceList()
        Handler().postDelayed({
            initGetLocation()
        }, 500)

        binding.btnAddPhotoFollowUpVisit.setOnClickListener {
            context?.let { ImagePickerHelper.openCameraFromFragment(it, mActivity, this, easyImage) }
        }
        binding.btnCheckIn.setOnClickListener {
            mActivity.showLoading()
            viewModel.currentLocation.value?.let {
                doCheckIn()
            } ?: run {
                getLocation {
                    doCheckIn()
                }
            }
        }
        binding.btnAddCustomer.setOnClickListener {
            viewModel.navController.navigate(R.id.action_followUpVisitFragment_to_addCustomerFragment)
        }

        binding.etVisitReason.addTextChangedListener(TextWatcherHelper {
            viewModel.validateFollowUpVisit()
        })
    }

    override fun onDestroy() {
        googleApiClient?.let {
            if (it.isConnected) {
                it.disconnect()
            }
        }
        super.onDestroy()
    }

    private fun doCheckIn() {
        with(viewModel){
            if (checkCustomerDistance()) {
                checkInFollowUpVisit(context, {
                    mActivity.hideLoading()
                    Handler().postDelayed({
                        if (navController.currentDestination?.id != R.id.signatureFormFragment ) {
                            navController.navigate(R.id.action_followUpVisitFragment_to_signatureFormFragment)
                        }

                    }, 500)
                }, {
                    mActivity.hideLoading()
                    context?.let { ctx -> CustomInfoDialog(ctx, it) }
                })
            } else {
                mActivity.hideLoading()
                context?.let { ctx ->
                    CustomInfoDialog(
                        ctx,
                        context?.getString(R.string.make_sure_location),
                        withoutCancel = true,
                        onDismiss = {
                            viewModel.canCheckIn.set(true)
                        })
                }
            }
        }
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

    private fun initSearch() {
        handler = Handler()
        binding.svPlace.setOnQueryTextListener(getQueryListener())

        viewModel.customerList.observe(viewLifecycleOwner, Observer { list ->
            mActivity.hideKeyboard()
            if (list.isEmpty()) {
                binding.rvCustomer.visibility = View.GONE
                binding.layoutCustomerNotFound.visibility = View.VISIBLE
                viewModel.followUpVisitFormVisibility.set(false)
            } else {
                binding.layoutCustomerNotFound.visibility = View.GONE
                binding.rvCustomer.visibility = View.VISIBLE
                viewModel.followUpVisitFormVisibility.set(false)
            }
            adapter?.list = viewModel.getCustomerListData {
                binding.rvCustomer.visibility = View.GONE
                viewModel.followUpVisitFormVisibility.set(true)
                binding.svPlace.setOnQueryTextListener(null)
                binding.svPlace.setQuery(viewModel.selectedCustomer.get()?.name, false)
                binding.svPlace.setOnQueryTextListener(getQueryListener())
            }
            adapter?.notifyDataSetChanged()
        })
    }

    fun setupPlaceList() {
        adapter = GenericAppAdapter(viewModel.getCustomerListData {
            viewModel.followUpVisitFormVisibility.set(true)
            binding.rvCustomer.visibility = View.GONE
            binding.btnCheckIn.requestFocus()
        })
        binding.rvCustomer.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCustomer.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(
            requestCode,
            resultCode,
            data,
            mActivity,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    viewModel.savePhotoFollowUpVisit(imageFiles[0].file)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    error.printStackTrace()
                }

                override fun onCanceled(source: MediaSource) {}
            })
    }

    fun getQueryListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.layoutCustomerNotFound.visibility = View.GONE
                viewModel.searchCustomers(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.layoutCustomerNotFound.visibility = View.GONE
                if (newText.isNullOrEmpty()) {
                    viewModel.customerList.value = mutableListOf()
                    binding.tvTitle.requestFocus()
                } else {
                    if (null != runnable) {
                        handler?.removeCallbacks(runnable)
                    }

                    runnable = Runnable {
                        if (!binding.btnCheckIn.isFocused) {
                            viewModel.searchCustomers(newText)
                        }
                    }
                    handler?.postDelayed(runnable, 800)
                }
                return true
            }

        }
    }

    private fun getLocation(cbOnSuccess: () -> Unit) {
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
                        fusedLocationClient?.lastLocation?.apply {
                            addOnSuccessListener { location: Location? ->
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
                                    CustomInfoDialog(
                                        ctx,
                                        ctx.getString(R.string.failed_fetch_your_location),
                                        withoutCancel = true
                                    )
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

    fun onUpdateLocation(location: LocationData) {
        viewModel.currentLocation.value = location
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
                mActivity.showFakeGpsDialog(FakeGps(true))
            }
        }
    }
}
