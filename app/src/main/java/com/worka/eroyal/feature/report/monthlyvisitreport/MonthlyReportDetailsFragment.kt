package com.worka.eroyal.feature.report.monthlyvisitreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentMonthlyReportDetailsBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 25/04/20.
 */

class MonthlyReportDetailsFragment : BaseFragment(), Paginate.Callbacks {

    private lateinit var binding: FragmentMonthlyReportDetailsBinding

    private val viewModel: ReportViewModel by sharedViewModel()

    private var adapter: GenericAppAdapter<SimpleViewModel> ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_monthly_report_details, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.tvTitle.text = context?.getString(R.string.monthly_visit_report)
        binding.toolbar.btnBack.setOnClickListener {
            ActivityController.finishToLeft(mActivity)
        }

        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageMonthlyDetails = false
            viewModel.pageMonthlyDetails = 1
        } else {
            adapter?.notifyDataSetChanged()
        }

        setupList()
        with(viewModel) {
            monthMonthlyReportDetails.set(arguments?.getString(Constants.MONTH))

            visitMonthlyReportViewModelList.observe(this@MonthlyReportDetailsFragment, Observer {
                adapter?.list = it.toList()
                adapter?.notifyDataSetChanged()
            })
        }
    }

    private fun getData() {
        viewModel.getMonthlyVisitDetails(
            arguments?.getInt(Constants.SALES_ID) ?: 0,
            arguments?.getString(Constants.MONTH),{
            }, {
                context?.let { it1 -> CustomInfoDialog(it1, it) }
            })
    }

    fun setupList () {
        binding.rvMonthlyReport.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMonthlyReport.adapter = adapter

        Paginate.with(binding.rvMonthlyReport, this)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageMonthlyDetails
    override fun isLoading(): Boolean = viewModel.isLoadingMonthlyDetails
    override fun onLoadMore() {
        getData()
    }

}
