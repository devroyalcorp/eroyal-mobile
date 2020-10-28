package com.worka.eroyal.feature.report.bybranch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentByBranchReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */

class ByBranchReportFragment: BaseFragment() {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentByBranchReportBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_by_branch_report, container, false)
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
        viewModel.getByBranchCustomer({
            mActivity.hideLoading()
            adapter?.let {
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
        adapter = GenericAppAdapter(viewModel.mostVisitedCustomers)
        binding.rvReportByBranch.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvReportByBranch.adapter = adapter
    }

}