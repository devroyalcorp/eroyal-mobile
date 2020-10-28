package com.worka.eroyal.feature.report.monthlyvisitreport

import com.worka.eroyal.R
import com.worka.eroyal.extensions.formatThousandSeparator
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
 * on 24/04/20.
 */
class MonthlyReportMyTeamItemViewModel(
    var id: Int?,
    var imgAvatar: String?,
    var name: String?,
    var brandName: String?,
    var areaName: String?,
    var totalVisitRKB: String,
    var totalFreeVisits: String,
    var failedTask: String,
    var totalSalesRevenue: String,
    var leftTitleList: ArrayList<String>?,
    var salesCountList: ArrayList<String>?,
    var onSelected: (Int?) -> Unit
) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_monthly_report_my_team

    fun onSelect() {
        onSelected.invoke(id)
    }

    fun leftHeaderAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        list.add(LeftTopCornerCellItemViewModel(getAppContext().getString(R.string.sales)))
        leftTitleList?.forEach {
            list.add(CellItemViewModel(it, true))
        }

        return GenericAppAdapter(list)
    }

    fun salesTableAdapter(): GenericAppAdapter<SimpleViewModel> {
        val list = arrayListOf<SimpleViewModel>()
        for (i in 1 until 6) {
            list.add(HeaderColumnItemViewModel(getAppContext().getString(R.string.week).plus(i.toString())))
        }

        salesCountList?.forEach {
            list.add(CellItemViewModel(it.replaceZeroWith("n/a").shortNumberFormat("n/a")))
        }
        return GenericAppAdapter(list)
    }

}
