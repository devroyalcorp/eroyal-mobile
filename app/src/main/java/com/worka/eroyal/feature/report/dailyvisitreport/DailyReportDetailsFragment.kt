package com.worka.eroyal.feature.report.dailyvisitreport

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
import com.worka.eroyal.base.Constants.DATE
import com.worka.eroyal.base.Constants.SALES_ID
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentDailyReportDetailsBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.DateUtils.DD_MM_YYYY
import com.worka.eroyal.utils.DateUtils.EEEE_D_MMM_YYYY
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/03/20.
 */
class DailyReportDetailsFragment : BaseFragment(), Paginate.Callbacks {

    private lateinit var binding: FragmentDailyReportDetailsBinding

    private val viewModel: ReportViewModel by sharedViewModel()

    private var adapter: GenericAppAdapter<SimpleViewModel> ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_report_details, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.dateDailyReportDetails.set(DateUtils.formateDate(arguments?.getString(DATE), DD_MM_YYYY, EEEE_D_MMM_YYYY))
        binding.toolbar.tvTitle.text = context?.getString(R.string.daily_visit_report)
        binding.toolbar.btnBack.setOnClickListener {
            ActivityController.finishToLeft(mActivity)
        }
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageDailyDetails = false
            viewModel.pageDailyDetails = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupList()
        with(viewModel) {
            visitDailyReportViewModelList.observe(this@DailyReportDetailsFragment, Observer {
                adapter?.list = it.toList()
                adapter?.notifyDataSetChanged()
            })
        }
    }

    private fun getData() {
        viewModel.getDailyReportDetails(
            arguments?.getInt(SALES_ID) ?: 0,
            arguments?.getString(DATE),{}, {
                context?.let { it1 -> CustomInfoDialog(it1, it) }
            })
    }

    fun setupList () {
        binding.rvActivityDailyReport.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvActivityDailyReport.adapter = adapter

        Paginate.with(binding.rvActivityDailyReport, this)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageDailyDetails
    override fun isLoading(): Boolean = viewModel.isLoadingDailyDetails
    override fun onLoadMore() {
        getData()
    }

}
