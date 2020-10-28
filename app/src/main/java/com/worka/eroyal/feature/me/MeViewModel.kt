package com.worka.eroyal.feature.me

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.me.BrandRevenue
import com.worka.eroyal.data.tasks.TaskResponseData
import com.worka.eroyal.extensions.*
import com.worka.eroyal.feature.tasks.TaskItemViewModel
import com.worka.eroyal.repository.MeRepository
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.DateUtils.MMMM
import com.worka.eroyal.utils.DateUtils.MMMM_YYYY
import com.worka.eroyal.utils.getDateFormat
import org.koin.core.inject
import java.util.*

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */
class MeViewModel(val app: Application) : BaseViewModel(app) {

    private val repository: MeRepository by inject()

    var customerVisitedList = arrayListOf<TaskResponseData>()
    var brandRevenueList = arrayListOf<BrandRevenue>()
    var customerFailedList = arrayListOf<TaskResponseData>()
    var customerFreeVisitList = arrayListOf<TaskResponseData>()
    var currentMonth = ObservableField("")
    var chartTitle = ObservableField("")
    var selectedDate = MutableLiveData<String>()
    var titleText = ObservableField("")
    var btnDatePickerVisibility = ObservableField(View.GONE)
    var currentVisitedCount = ObservableField(0)
    var targetVisitCount = ObservableField(0)
    var failedVisitCount = ObservableField(0)
    var freeVisitCount = ObservableField(0)
    var emptyVisitedVisibility = ObservableField<Int>(View.GONE)
    var emptyFailedVisibility = ObservableField<Int>(View.GONE)
    var emptyFreeVisitVisibility = ObservableField<Int>(View.GONE)
    var visitChartData = ObservableField<Array<Int>>(arrayOf(0, 0))
    var rkbChartData = ObservableField<Array<Int>>(arrayOf(0, 0))
    var salesPercentage = ObservableField("")
    var salesValue = ObservableField("")
    var salesTotal = ObservableField("")
    var visitPercentage = ObservableField("")
    var visitValue = ObservableField("")
    var visitTotal = ObservableField("")
    var visitCircleChartData = ObservableField(0)
    var salesCircleChartData = ObservableField(0)
    var rkbColorLineChart = ObservableField<Int>(R.color.colorGrey)
    var visitColorLineChart = ObservableField<Int>(R.color.colorGrey)
    var chartDatas = ObservableField<Array<Array<Int>?>>()
    var colorLineCharts = ObservableField<Array<Int?>>()

    fun chartColor(array: Array<Int>): Int {
        return if (array.size > 1) {
            val startValue = array.first()
            val endValue = array.last()
            if (startValue < endValue) {
                R.color.colorGreen
            } else {
                R.color.colorRed
            }
        } else {
            R.color.colorGrey
        }
    }

    fun getCustomerTaskList(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getCustomerVisitedList(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(), {
                customerVisitedList.clear()
                customerFailedList.clear()
                customerFreeVisitList.clear()
                it?.statistic?.visited?.visitedList?.let { it1 -> customerVisitedList.addAll(it1) }
                it?.statistic?.failed?.visitedList?.let { it1 -> customerFailedList.addAll(it1) }
                it?.statistic?.freeVisit?.visitedList?.let { it1 -> customerFreeVisitList.addAll(it1) }
                currentVisitedCount.set(it?.statistic?.visited?.value)
                targetVisitCount.set(it?.statistic?.visited?.total)
                failedVisitCount.set(it?.statistic?.failed?.value)
                freeVisitCount.set(it?.statistic?.freeVisit?.value)
                currentMonth.set(it?.statistic?.month)
                chartTitle.set(DateUtils.formateDate(it?.statistic?.month, MMMM_YYYY, MMMM).visitEndPrefix())
                titleText.set(currentMonth.get())

                setEmptyStateVisibility()
                getRevenueBrands({
                    cbOnSuccess.invoke()
                },{
                    cbOnError.invoke(it)
                })
            },
            {
                cbOnError.invoke(it)
            })
    }

    fun getVisitedData(): ArrayList<TaskItemViewModel> {
        val list = arrayListOf<TaskItemViewModel>()
        customerVisitedList.forEach {
            list.add(TaskItemViewModel(
                it.id,
                TaskItemViewModel.TaskState.COMPLETE.state,
                it.customer?.name,
                it.createdBy?.name,
                View.GONE,
                getAppContext().getString(R.string.visited_at) + " " + it.checkOut,
                true,
                it.description,
                it.visitResultStatus
            ) {})
        }
        return list
    }

    fun getFreeVisitData(): ArrayList<TaskItemViewModel> {
        val list = arrayListOf<TaskItemViewModel>()
        customerFreeVisitList.forEach { task ->
            list.add(TaskItemViewModel(
                task.id,
                TaskItemViewModel.TaskState.COMPLETE.state,
                task.customer?.name,
                "",
                View.GONE,
                getAppContext().getString(R.string.visited_at) + " " + task.checkOut,
                true,
                task.description,
                task.visitResultStatus
            ) {})
        }
        return list
    }

    fun getFailedData(): ArrayList<TaskItemViewModel> {
        val list = arrayListOf<TaskItemViewModel>()
        customerFailedList.forEach { task ->
            list.add(TaskItemViewModel(
                task.id,
                TaskItemViewModel.TaskState.COMPLETE.state,
                task.customer?.name,
                task.createdBy?.name.taskByPrefix(),
                View.GONE,
                "",
                true,
                task.description,
                task.visitResultStatus
            ) {})
        }
        return list
    }

    fun getStatistic(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getStatistic(
            sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(),
            DateUtils.formateDate(selectedDate.value,
                DateUtils.EEEE_D_MMM_YYYY,
                DateUtils.DD_MM_YYYY), {
                it?.statistic?.sales?.let { sales ->
                    salesPercentage.set(sales.percentage.toString().percentPrefix())
                    salesValue.set(sales.value.toString())
                    salesTotal.set(sales.total.toString().ofPrefix())
                    salesCircleChartData.set(sales.percentage)
                }
                it?.statistic?.visited?.let { visit ->
                    visitChartData.set(visit.chartVisit)
                    rkbChartData.set(visit.chartRKB)
                    visitPercentage.set(visit.percentage.toString().percentPrefix())
                    visitValue.set(visit.value.toString())
                    visitTotal.set(visit.total.toString().ofPrefix())
                    rkbColorLineChart.set(chartColor(rkbChartData.get()?: arrayOf()))
                    visitColorLineChart.set(chartColor(visitChartData.get()?: arrayOf()))
                    visitCircleChartData.set(visit.percentage)
                }
                colorLineCharts.set(arrayOf(R.color.colorRed, R.color.colorGreen))
                chartDatas.set(arrayOf(rkbChartData.get(), visitChartData.get()))
                cbOnSuccess.invoke()
            },
            {
                cbOnError.invoke(it)
            })
    }

    private fun setEmptyStateVisibility() {
        if (customerVisitedList.isEmpty()) {
            emptyVisitedVisibility.set(View.VISIBLE)
        } else {
            emptyVisitedVisibility.set(View.GONE)
        }

        if (customerFailedList.isEmpty()) {
            emptyFailedVisibility.set(View.VISIBLE)
        } else {
            emptyFailedVisibility.set(View.GONE)
        }

        if (customerFreeVisitList.isEmpty()) {
            emptyFreeVisitVisibility.set(View.VISIBLE)
        } else {
            emptyFreeVisitVisibility.set(View.GONE)
        }
    }

    private fun getRevenueBrands(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getBrandRevenue(currentMonth.get().orEmpty(), sessionStorage.getAccessToken(),
            sessionStorage.getClient(),
            sessionStorage.getUid(), {
                brandRevenueList.clear()
                it?.brands?.let { it1 -> brandRevenueList.addAll(it1) }
                cbOnSuccess.invoke()
            }, {
                cbOnError.invoke(it)
            })
    }

    fun getRevenueBrandList(): ArrayList<BrandRevenueItemViewModel> {
        val list = arrayListOf<BrandRevenueItemViewModel>()
        brandRevenueList.forEach { brand ->
            list.add(
                BrandRevenueItemViewModel(
                    brand.brandName,
                    brand.currentMonth,
                    brand.previousMonth,
                    brand.currentMonthValue?.formatThousandSeparator(),
                    brand.revenueState,
                    brand.yoyState,
                    brand.previousMonthValue?.formatThousandSeparator(),
                    brand.percentage?.toPriceInt(),
                    brand.target?.formatThousandSeparator(),
                    brand.previousYear,
                    brand.previousYearValue?.formatThousandSeparator(),
                    brand.yoyPercentage.percentPrefix()
                )
            )
        }
        return list
    }

}
