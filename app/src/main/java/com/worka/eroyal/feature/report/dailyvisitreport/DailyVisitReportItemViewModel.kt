package com.worka.eroyal.feature.report.dailyvisitreport

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/03/20.
 */
class DailyVisitReportItemViewModel (var icon: Int = R.drawable.ic_location_black, var customerName: String?, var dateTime: String?, var spent: String?,
                                     var notes: String?, var isFreeVisit: Boolean = false, var lineVisibility: Int = View.VISIBLE): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_daily_visit_report
}
