package com.worka.eroyal.feature.report.monthlyvisitreport

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.Constants
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/04/20.
 */
class MonthlyVisitReportDetailsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        FragmentController.navigateTo(this, MonthlyReportDetailsFragment::class.java.name, Bundle().apply {
            putInt(Constants.SALES_ID, intent.extras?.getInt(Constants.SALES_ID, 0) ?:0)
            putString(Constants.MONTH, intent.extras?.getString(Constants.MONTH))
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishToLeft(this)
    }
}
