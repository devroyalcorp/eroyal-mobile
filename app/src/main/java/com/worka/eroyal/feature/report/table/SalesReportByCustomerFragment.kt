package com.worka.eroyal.feature.report.table

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.OptionListDialog
import com.worka.eroyal.data.report.filter.CustomerReportSortBy
import com.worka.eroyal.data.report.filter.SortDirection
import com.worka.eroyal.databinding.FragmentSalesReportByCustomerBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.OptionItemViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 13/04/20.
 */

class SalesReportByCustomerFragment : BaseFragment(), Paginate.Callbacks {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentSalesReportByCustomerBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var sortByDialog: OptionListDialog? = null
    private var sortDirectionDialog: OptionListDialog? = null
    private var checkBoxFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_sales_report_by_customer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageSalesByCustomer = false
            viewModel.pageSalesByCustomer = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupTable()

        viewModel.salesReportByCustomerViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })

        viewModel.selectedMonth.observe(this, Observer {
            viewModel.isLastPageSalesByCustomer = false
            viewModel.pageSalesByCustomer = 1
            adapter?.list = arrayListOf()
            getData()
        })

        with(binding) {
            btnSortBy.setOnClickListener {
                sortByDialog?.show()
            }

            btnSortDirection.setOnClickListener {
                sortDirectionDialog?.show()
            }
            btnAreaFilter.setOnClickListener {
                checkBoxFilterDialog?.let{
                    it.show()
                } ?: run {
                    context?.let { ctx -> CustomInfoDialog(ctx, ctx.getString(R.string.filter_not_ready_yet)) }
                }
            }

            btnBrandFilter.setOnClickListener {
                brandFilterDialog?.let{
                    it.show()
                } ?: run {
                    context?.let { ctx -> CustomInfoDialog(ctx, ctx.getString(R.string.filter_not_ready_yet)) }
                }
            }
        }

        initSearch()
        setupSortDialog()
        setupAreaFilterDialog()
        setupBrandFilterDialog()
    }

    private fun initSearch() {
        handler = Handler()
        with(binding) {
            svReportCustomer.setOnQueryTextListener(getQueryListener())
        }
    }

    private fun getQueryListener(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                with(viewModel) {
                    searchKeywordReportCustomer.set(query)
                    onRefresh()
                    binding.svReportCustomer.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                with(viewModel) {
                    if (null != runnable) {
                        handler?.removeCallbacks(runnable)
                    }

                    runnable = Runnable {
                        searchKeywordReportCustomer.set(newText)
                        onRefresh()
                        binding.svReportCustomer.clearFocus()
                    }
                    handler?.postDelayed(runnable, 1000)
                }
                return true
            }
        }
    }

    private fun setupAreaFilterDialog() {
        context?.let { ctx ->
            checkBoxFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_area_to_show), viewModel.areaSalesByCustomerFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    areaSalesByCustomerFilterList = list
                    setAreaSalesByCustomerFilterTitle()
                    pageSalesByCustomer = 1
                }
                getData()
            }
        }
    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_brand_to_show), viewModel.brandSalesByCustomerFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    brandSalesByCustomerFilterList = list
                    setBrandSalesByCustomerFilterTitle()
                    pageSalesByCustomer = 1
                }
                getData()
            }
        }
    }

    private fun setupSortDialog() {
        context?.let {  ctx ->
            val sortByList = arrayListOf<OptionItemViewModel>()
            enumValues<CustomerReportSortBy>().forEach { sortBy ->
                sortByList.add(OptionItemViewModel(sortBy.type, ctx.getString(sortBy.titleRes)) {
                    viewModel.selectedSortBySalesReportCustomer.set(sortBy)
                    viewModel.sortByTitleSalesReportCustomer.set(ctx.getString(R.string.sort_by).plus("    ").plus(ctx.getString(sortBy.titleRes)))
                    sortByDialog?.dismiss()
                    onRefresh()
                })
            }
            sortByDialog = OptionListDialog(ctx, ctx.getString(R.string.sort_by),sortByList)

            val sortDirectionList = arrayListOf<OptionItemViewModel>()
            enumValues<SortDirection>().forEach { sortDirection ->
                sortDirectionList.add(OptionItemViewModel(sortDirection.type, ctx.getString(sortDirection.titleRes)) {
                    viewModel.selectedSortDirectionSalesReportCustomer.set(sortDirection)
                    viewModel.sortDirectionTitleSalesReportCustomer.set(ctx.getString(R.string.sort).plus("    ").plus(ctx.getString(sortDirection.titleRes)))
                    sortDirectionDialog?.dismiss()
                    onRefresh()
                })
            }
            sortDirectionDialog = OptionListDialog(ctx, ctx.getString(R.string.sort), sortDirectionList)
        }
    }

    private fun onRefresh() {
        viewModel.pageSalesByCustomer = 1
        viewModel.isLastPageSalesByCustomer = false
        getData()
    }


    fun getData(){
        viewModel.getReportSalesByCustomer {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    private fun setupTable() {
        binding.rvTableReport.layoutManager = GridLayoutManager(context, Constants.SALES_REPORT_BY_CUSTOMER_COLUMN_SIZE)
        binding.rvTableReport.adapter = adapter

        Paginate.with(binding.rvTableReport, this)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageSalesByCustomer
    override fun isLoading(): Boolean = viewModel.isLoadingSalesByCustomer
    override fun onLoadMore() {
        getData()
    }
}
