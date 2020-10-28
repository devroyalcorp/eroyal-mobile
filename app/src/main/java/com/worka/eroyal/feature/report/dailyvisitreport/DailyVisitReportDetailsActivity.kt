package com.worka.eroyal.feature.report.dailyvisitreport

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.Constants.DATE
import com.worka.eroyal.base.Constants.SALES_ID
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController
import com.worka.eroyal.feature.report.ReportViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/03/20.
 */
class DailyVisitReportDetailsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        FragmentController.navigateTo(this, DailyReportDetailsFragment::class.java.name, Bundle().apply {
            putInt(SALES_ID, intent.extras?.getInt(SALES_ID, 0) ?:0)
            putString(DATE, intent.extras?.getString(DATE))
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishToLeft(this)
    }
}
