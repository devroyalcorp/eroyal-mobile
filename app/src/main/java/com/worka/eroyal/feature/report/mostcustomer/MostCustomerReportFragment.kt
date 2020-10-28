package com.worka.eroyal.feature.report.mostcustomer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentMostCustomerReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-16.
 */
class MostCustomerReportFragment : BaseFragment() {

    private val viewModel: ReportViewModel by sharedViewModel()
    lateinit var binding: FragmentMostCustomerReportBinding
    private var mostAdapter: GenericAppAdapter<SimpleViewModel>? = null
    private var plannedAdapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_most_customer_report, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.selectedMonth.observeForever {
            getData()
        }
    }

    fun getData(){
        mActivity.showLoading()
        viewModel.getMostVisitedCustomer({
            mActivity.hideLoading()
            mostAdapter?.let {
                it.notifyDataSetChanged()
            }?: kotlin.run {
                setupList()
            }
        }, {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })

        viewModel.getMostPlannedCustomer({
            mActivity.hideLoading()
            plannedAdapter?.let {
                it.notifyDataSetChanged()
            }?: kotlin.run {
                setupList()
            }
        }, {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
    }

    fun setupList() {
        mostAdapter = GenericAppAdapter(viewModel.mostVisitedCustomers)
        binding.rvMostVisited.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMostVisited.adapter = mostAdapter

        plannedAdapter = GenericAppAdapter(viewModel.mostPlannedCustomers)
        binding.rvMostPlanned.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMostPlanned.adapter = plannedAdapter
    }

}