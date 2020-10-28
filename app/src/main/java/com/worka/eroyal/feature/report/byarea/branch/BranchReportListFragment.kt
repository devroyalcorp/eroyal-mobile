package com.worka.eroyal.feature.report.byarea.branch

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
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentBranchReportListBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-29.
 */
class BranchReportListFragment: BaseFragment(), Paginate.Callbacks {
    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentBranchReportListBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_branch_report_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.titlePage.value = context?.getString(R.string.branches)
        viewModel.onSelectedReportType.value = ""
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageBranch = false
            viewModel.pageBranch = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupList()

        viewModel.branchesReportViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
    }

    fun getData(){
        viewModel.isLoadingBranch = true
        viewModel.getBranchReport {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    fun setupList(){
        binding.rvAreaReport.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvAreaReport.adapter = adapter

        Paginate.with(binding.rvAreaReport, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageBranch

    override fun isLoading(): Boolean = viewModel.isLoadingBranch

    override fun onLoadMore() {
        getData()
    }
}
