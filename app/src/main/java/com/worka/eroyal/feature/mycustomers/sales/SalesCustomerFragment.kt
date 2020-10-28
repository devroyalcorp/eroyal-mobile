package com.worka.eroyal.feature.mycustomers.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentSalesCustomerBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-29.
 */

class SalesCustomerFragment : BaseFragment() {

    private lateinit var binding: FragmentSalesCustomerBinding

    private var adapter: GenericAppAdapter<SalesCustomerItemViewModel>? = null

    private val viewModel: MyCustomerViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_customer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.scrollView.isNestedScrollingEnabled = true
        binding.rvSales.isNestedScrollingEnabled = true
        viewModel.setDefaultMonth()
        viewModel.getSalesCustomer({
            setupList()
            mActivity.hideLoading()
        }, {
            mActivity.hideLoading()
        })
    }

    private fun setupList() {
        adapter = GenericAppAdapter(viewModel.salesViewModelList)
        with(binding) {
            rvSales.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvSales.adapter = adapter
        }
    }
}
