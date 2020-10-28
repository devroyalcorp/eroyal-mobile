package com.worka.eroyal.feature.report.monthlyvisitreport

import com.worka.eroyal.R
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.replaceZeroWith
import com.worka.eroyal.extensions.shortNumberFormat
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.LeftTopCornerCellItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/04/20.
 */

class MonthlyVisitReportItemViewModel(
    var name: String?,
    var totalVisitRKB: String,
    var totalFreeVisits: String,
    var failedTask: String,
    var visitCountList: ArrayList<String>?,
    var leftSalesList: ArrayList<String>?,
    var salesCountList: ArrayList<String>?,
    var salesBrandList: ArrayList<String>?,
    var salesBrandCountList: ArrayList<String>?,
    var onSelected: (Int?) -> Unit
) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_monthly_visit_report

    fun leftVisitAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        list.add(HeaderColumnItemViewModel(""))
        list.add(CellItemViewModel(getAppContext().getString(R.string.visit_rkb), true))
        list.add(CellItemViewModel(getAppContext().getString(R.string.free_visit), true))
        list.add(CellItemViewModel(getAppContext().getString(R.string.failed_task), true))

        return GenericAppAdapter(list)
    }

    fun visitTableAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        for (i in 1 until 6) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.week).plus(i.toString())))
        }

        visitCountList?.forEach {
            list.add(CellItemViewModel(it.replaceZeroWith("n/a").shortNumberFormat("n/a")))
        }
        return GenericAppAdapter(list)
    }

    fun leftSalesAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        if (!leftSalesList.isNullOrEmpty()) {
            list.add(LeftTopCornerCellItemViewModel(getAppContext().getString(R.string.sales)))
        }
        leftSalesList?.forEach {
            list.add(CellItemViewModel(it, true))
        }

        return GenericAppAdapter(list)
    }

    fun salesTableAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        for (i in 1 until 6) {
            list.add(HeaderColumnItemViewModel(""))
        }
        salesCountList?.forEach {
            list.add(CellItemViewModel(it))
        }
        return GenericAppAdapter(list)
    }

    fun salesBrandTableAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        salesBrandList?.let { arrayList ->
            arrayList.forEachIndexed { index, s ->
                when (index) {
                    0 -> {
                        list.add(CellRadiusItemViewModel(s, false))
                    }
                    arrayList.size - 1 -> {
                        list.add(CellRadiusItemViewModel(s, true))
                    }
                    else -> {
                        list.add(GreenCellItemViewModel(s))
                    }
                }
            }
        }

        salesBrandCountList?.forEach {
            list.add(CellItemViewModel(it.shortNumberFormat("n/a")))
        }

        return GenericAppAdapter(list)
    }

}
