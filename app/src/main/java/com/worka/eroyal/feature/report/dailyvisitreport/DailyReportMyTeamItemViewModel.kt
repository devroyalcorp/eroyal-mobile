package com.worka.eroyal.feature.report.dailyvisitreport

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 23/03/20.
 */

class DailyReportMyTeamItemViewModel(
    var id: Int?,
    var imgAvatar: String?,
    var name: String?,
    var brandName: String?,
    var areaName: String?,
    var visitCount: String,
    var otherVisits: String,
    var failedTask: String,
    var onSelected: (Int?) -> Unit
) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_daily_report_my_team

    fun onSelect() {
        onSelected.invoke(id)
    }

}
