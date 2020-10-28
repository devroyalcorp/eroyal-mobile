package com.worka.eroyal.feature.report.bysales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentBySalesReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-20.
 */
class BySalesReportFragment: BaseFragment(){
    private lateinit var binding: FragmentBySalesReportBinding
    private val viewModel: ReportViewModel by sharedViewModel()
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_by_sales_report, container,false)
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
        viewModel.getBySalesReport({
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
        adapter = GenericAppAdapter(viewModel.bySalesReports)
        binding.rvReportBySales.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvReportBySales.adapter = adapter
    }
}