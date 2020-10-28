package com.worka.eroyal.feature.me

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants.DECREASED
import com.worka.eroyal.base.Constants.INCREASED
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.percentPrefix
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-27.
 */
class BrandRevenueItemViewModel(
    var brandName: String?,
    var currentMonth: String?,
    var prevMonth: String?,
    var currentMonthValue: String?,
    var revenueState: String?,
    var yoyState: String?,
    var prevMonthValue: String?,
    var targetPercentage: Int?,
    var target: String?,
    var prevYear: String?,
    var prevYearValue: String?,
    var yoyPercentage: String?
) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_brand_revenue

    fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return displayMetrics.widthPixels - ((displayMetrics.widthPixels/100) * 10)
    }

    fun getTargetPercentageText(): String {
        return targetPercentage.toString().percentPrefix()
    }

    fun getArrowRevenueState(): Int? {
        return when (revenueState) {
            DECREASED -> R.drawable.ic_arrow_down_red
            INCREASED -> R.drawable.ic_arrow_up_green
            else -> null
        }
    }

    fun getArrowYoYState(): Int? {
        return when (yoyState) {
            DECREASED -> R.drawable.ic_arrow_down_red
            INCREASED -> R.drawable.ic_arrow_up_green
            else -> null
        }
    }
}
