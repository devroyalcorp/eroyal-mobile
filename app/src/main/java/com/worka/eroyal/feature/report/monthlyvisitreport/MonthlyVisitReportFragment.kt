package com.worka.eroyal.feature.report.monthlyvisitreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.component.monthpicker.MonthPickerDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentMonthlyVisitReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 24/04/20.
 */

class MonthlyVisitReportFragment: BaseFragment(), Paginate.Callbacks  {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentMonthlyVisitReportBinding

    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private var checkBoxFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null
    private val datePickerDialog: MonthPickerDialog? by lazy {
        context?.let { ctx ->
            MonthPickerDialog(ctx) { monthYear ->
                with(viewModel) {
                    viewModel.dateMonthlyFilterTitle.set(monthYear)
                    viewModel.selectedMonthMonthlyReport.set(monthYear)
                    viewModel.monthMonthlyReportDetails.set(monthYear)
                    pageMonthlyReport = 1
                    getData()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_monthly_visit_report, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageMonthlyReport = false
            viewModel.pageMonthlyReport = 1
        } else {
            adapter?.notifyDataSetChanged()
        }

        with(viewModel) {
            setupAreaFilterDialog()
            setupBrandFilterDialog()
            setupList()

            monthlyReportMyTeamViewModelList.observe(this@MonthlyVisitReportFragment, androidx.lifecycle.Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            })

            onOpenMonthlyReportDetails.observe(this@MonthlyVisitReportFragment, androidx.lifecycle.Observer {
                ActivityController.navigateToRight(mActivity, MonthlyVisitReportDetailsActivity::class.java, bundle = Bundle().apply {
                    putInt(Constants.SALES_ID, it ?: 0)
                    putString(Constants.MONTH, this@with.selectedMonthMonthlyReport.get())
                })
            })
        }

        with(binding) {
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

            btnDateFilter.setOnClickListener {
                datePickerDialog?.show()
            }
        }
    }

    private fun getData(){
        viewModel.isLoadingMonthlyReport = true
        viewModel.getMonthlyReport {
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    private fun setupAreaFilterDialog() {
        context?.let { ctx ->
            checkBoxFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_area_to_show), viewModel.areaMonthlyFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    areaMonthlyFilterList = list
                    setAreaMonthlyFilterTitle()
                    pageMonthlyReport = 1
                }
                getData()
            }
        }
    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_brand_to_show), viewModel.brandMonthlyFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    brandMonthlyFilterList = list
                    setBrandMonthlyFilterTitle()
                    pageMonthlyReport = 1
                }
                getData()
            }
        }
    }

    fun setupList(){
        binding.rvMonthlyReportVisit.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMonthlyReportVisit.adapter = adapter

        Paginate.with(binding.rvMonthlyReportVisit, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageMonthlyReport

    override fun isLoading(): Boolean = viewModel.isLoadingMonthlyReport

    override fun onLoadMore() {
        getData()
    }

}
