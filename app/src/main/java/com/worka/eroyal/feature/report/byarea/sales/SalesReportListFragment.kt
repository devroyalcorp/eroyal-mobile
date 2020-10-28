package com.worka.eroyal.feature.report.byarea.sales

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
import com.worka.eroyal.databinding.FragmentSalesReportListBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-03-02.
 */
class SalesReportListFragment: BaseFragment(), Paginate.Callbacks {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentSalesReportListBinding
    private var adapter: GenericAppAdapter<BranchSalesReportItemViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_report_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.titlePage.value = context?.getString(R.string.sales)
        viewModel.onSelectedReportType.value = ""
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageSales = false
            viewModel.pageSales = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupList()
        viewModel.branchSalesReportViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
    }

    fun getData(){
        viewModel.isLoadingSales = true
        viewModel.getBranchSalesReport {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    fun setupList(){
        binding.rvSalesReport.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvSalesReport.adapter = adapter

        Paginate.with(binding.rvSalesReport, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageSales

    override fun isLoading(): Boolean = viewModel.isLoadingSales

    override fun onLoadMore() {
        getData()
    }

}

