package com.worka.eroyal.feature.report.dailyvisitreport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants.DATE
import com.worka.eroyal.base.Constants.SALES_ID
import com.worka.eroyal.component.CheckBoxFilterDialog
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.DatePickerDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentDailyVisitReportBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.feature.report.byarea.CheckBoxFilterItemViewModel
import com.worka.eroyal.utils.DateUtils.DD_MM_YYYY
import com.worka.eroyal.utils.DateUtils.EEEE_D_MMM_YYYY
import com.worka.eroyal.utils.getDateFormat
import com.worka.eroyal.utils.getDayMonth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 21/03/20.
 */

class DailyVisitReportFragment: BaseFragment(), Paginate.Callbacks  {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentDailyVisitReportBinding

    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private var checkBoxFilterDialog: CheckBoxFilterDialog? = null
    private var brandFilterDialog: CheckBoxFilterDialog? = null
    private val datePickerDialog: DatePickerDialog? by lazy {
        context?.let { ctx ->
            val minDate = Calendar.getInstance().time
            minDate.month = minDate.month - 3
            val maxDate = Calendar.getInstance().time
            DatePickerDialog(ctx, minDate = minDate.time, maxDate = maxDate.time) {
                with(viewModel) {
                    viewModel.dateDailyFilterTitle.set(it.getDayMonth())
                    viewModel.selectedDateDailyReport.set(it.getDateFormat(DD_MM_YYYY))
                    viewModel.dateDailyReportDetails.set(it.getDateFormat(EEEE_D_MMM_YYYY))
                    pageDailyReport = 1
                    getData()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_visit_report, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageDailyReport = false
            viewModel.pageDailyReport = 1
        } else {
            adapter?.notifyDataSetChanged()
        }

        with(viewModel) {
            setupAreaFilterDialog()
            if (brandDailyFilterList.isNotEmpty()) {
                setupList()
            }

            setupBrandFilterDialog()
            if (areaDailyFilterList.isNotEmpty()) {
                setupList()
            }

            dailyReportMyTeamViewModelList.observe(this@DailyVisitReportFragment, androidx.lifecycle.Observer {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            })

            onOpenDailyReportDetails.observe(this@DailyVisitReportFragment, androidx.lifecycle.Observer {
                ActivityController.navigateToRight(mActivity, DailyVisitReportDetailsActivity::class.java, bundle = Bundle().apply {
                    putInt(SALES_ID, it ?: 0)
                    putString(DATE, this@with.selectedDateDailyReport.get())
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
        viewModel.isLoadingDailyReport = true
        viewModel.getDailyReport {
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    private fun setupAreaFilterDialog() {
        context?.let { ctx ->
            checkBoxFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_area_to_show), viewModel.areaDailyFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    areaDailyFilterList = list
                    setAreaDailyFilterTitle()
                    pageDailyReport = 1
                }
                getData()
            }
        }
    }

    private fun setupBrandFilterDialog() {
        context?.let { ctx ->
            brandFilterDialog = CheckBoxFilterDialog(ctx, ctx.getString(R.string.select_which_brand_to_show), viewModel.brandDailyFilterList) {
                val list = arrayListOf<CheckBoxFilterItemViewModel>()
                it?.forEachIndexed { index, filterAreaItemViewModel ->
                    list.add(filterAreaItemViewModel)
                }
                with(viewModel) {
                    brandDailyFilterList = list
                    setBrandDailyFilterTitle()
                    pageDailyReport = 1
                }
                getData()
            }
        }
    }

    fun setupList(){
        binding.rvDailyReportVisit.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvDailyReportVisit.adapter = adapter

        Paginate.with(binding.rvDailyReportVisit, this)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageDailyReport

    override fun isLoading(): Boolean = viewModel.isLoadingDailyReport

    override fun onLoadMore() {
        getData()
    }

}
