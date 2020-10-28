package com.worka.eroyal.feature.tasks.visit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.bus.FakeGps
import com.worka.eroyal.databinding.FragmentPlacePickerBinding
import com.worka.eroyal.feature.tasks.TasksViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-25.
 */
class PlacePickerFragment : BaseFragment(), OnMapReadyCallback {

    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentPlacePickerBinding

    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_place_picker, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLocation {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
        binding.viewModel = viewModel

        binding.btnPick.setOnClickListener {
            val latLng = mMap?.cameraPosition?.target?.latitude?.let { lat ->
                mMap?.cameraPosition?.target?.longitude?.let { long ->
                    LatLng(
                        lat,
                        long
                    )
                }
            }
            viewModel.newCustomerLocation = latLng
            viewModel.navController.popBackStack()
        }


    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        viewModel.currentLocation.value?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
            mMap?.animateCamera(cameraUpdate)
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
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return@let
                        }
                        FusedLocationProviderClient(ctx).lastLocation?.apply {
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

    fun onUpdateLocation(location: LocationData) {
        Log.e("Location", "latitude: ${location.latitude} longitude: ${location.longitude}")
        if (viewModel.currentLocation.value == null) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
            mMap?.moveCamera(cameraUpdate)
        }

        viewModel.currentLocation.value = location
    }

}
