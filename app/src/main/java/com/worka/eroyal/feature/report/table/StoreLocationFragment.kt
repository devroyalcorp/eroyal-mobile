package com.worka.eroyal.feature.report.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.databinding.FragmentStoreLocationBinding
import com.worka.eroyal.extensions.filterEmpty
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 14/04/20.
 */
class StoreLocationFragment : BaseFragment(), OnMapReadyCallback {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentStoreLocationBinding

    private var mMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_location, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity.showLoading()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getCustomerLocation({
           setupMap(it.customers)
        },{
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
    }

    private fun setupMap(customers: ArrayList<Customer>) {
        val builder = LatLngBounds.Builder()
        customers.forEach {
            val latLngCustomer = LatLng(it.latitude.filterEmpty("0").toDouble(), it.longitude.filterEmpty("0").toDouble())
            builder.include(latLngCustomer)
            mMap?.addMarker(MarkerOptions().position(latLngCustomer).title(it.name))
        }

        mActivity.hideLoading()

        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 25)
        mMap?.animateCamera(cameraUpdate)
    }
}
