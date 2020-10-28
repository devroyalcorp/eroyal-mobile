package com.worka.eroyal.feature.clockinout

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.bus.FakeGps
import com.worka.eroyal.databinding.FragmentClockInOutBinding
import com.worka.eroyal.extensions.getAppContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ClockInOutFragment : BaseFragment(), GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener {
    private val TAG = ClockInOutFragment::class.java.simpleName

    private val viewModel: ClockInOutViewModel by sharedViewModel()
    private lateinit var binding: FragmentClockInOutBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_in_out, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        initGetLocation()
        setupCamera()

        val typeface = context?.let { ResourcesCompat.getFont(it, R.font.neutrif_pro_semibold) }
        binding.clock.typeface = typeface

        binding.btnShutter.setOnClickListener {
            binding.camera.takePictureSnapshot()
        }

        binding.btnClockInOut.setOnClickListener {

            when(viewModel.labelClockInOutButton.get()) {
                context?.getString(R.string.clock_in) -> {
                    clockInOut()
                }

                context?.getString(R.string.clock_out) -> {
                    context?.let { ctx ->
                        CustomInfoDialog(ctx, context?.getString(R.string.are_you_sure_want_to_do_clock_out), onDismiss = {
                            clockInOut()
                        })
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        googleApiClient?.let {
            if (it.isConnected) {
                it.disconnect()
            }
        }
        super.onDestroy()
    }
    
    fun setupCamera(){
        mActivity.requestForPermissions(
            Constants.CAMERA_STORAGE_PERMISSION,
            Constants.REQUEST_CAMERA, grantResults = { _, permissionResult ->
                if (permissionResult == PermissionResult.GRANTED) {
                    mActivity.showLoading()
                    initCamera()
                    getData()
                } else {
                    context?.let { ctx -> CustomInfoDialog(ctx, ctx.getString(R.string.storage_camera_permission_required)) }
                }
            })
    }

    private fun initCamera() {
        binding.camera.facing = Facing.FRONT
        binding.camera.addCameraListener(object : CameraListener(){
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap {
                    it?.let { bitmap -> viewModel.saveBitmap(bitmap) }
                    binding.camera.close()
                    binding.camera.open()
                }
               viewModel.isImageCaptured.set(true)
            }
        })
    }

    fun clockInOut() {
        context?.let { ctx ->
            mActivity.showLoading()
            viewModel.currentLocation.value?.let {
                doClockInOut()
            } ?: run {
                getLocation {
                    doClockInOut()
                }
            }
        }
    }

    private fun doClockInOut() {
        context?.let { ctx ->
            viewModel.clockInOut(ctx, {
                viewModel.buttonRetakeVisibility.set(View.INVISIBLE)
                getData()
            }, {
                mActivity.hideLoading()
                CustomInfoDialog(ctx, it)
            })
        }
    }

    fun getData(){
        viewModel.getAbsence{ mActivity.hideLoading() }
    }

    private fun getLocation(cbOnSuccess: () -> Unit) {
        mActivity.requestForPermissions(
            Constants.LOCATION_PERMISSIONS,
            Constants.REQUEST_LOCATION,
            grantResults = { _, permissionResult ->
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
                                        Handler().postDelayed({
                                                cbOnSuccess.invoke()
                                            }, 500
                                        )
                                    } else {
                                        mActivity.hideLoading()
                                        mActivity.showFakeGpsDialog(FakeGps(true))
                                    }
                                } ?: run {
                                    onFailedGetLocation()
                                }
                            }
                            addOnFailureListener {
                                onFailedGetLocation()
                            }
                        }
                    }
                }
            })
    }

    private fun onFailedGetLocation() {
        mActivity.hideLoading()
        context?.let { ctx ->
            CustomInfoDialog(
                ctx,
                ctx.getString(R.string.failed_fetch_your_location),
                withoutCancel = true
            )
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
                        if (googleApiClient?.isConnected == false) {
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


    fun onUpdateLocation(location: LocationData){
        viewModel.currentLocation.value = location
    }

    override fun onResume() {
        super.onResume()
        binding.camera.open()
    }

    override fun onPause() {
        binding.camera.close()
        super.onPause()
    }

}
