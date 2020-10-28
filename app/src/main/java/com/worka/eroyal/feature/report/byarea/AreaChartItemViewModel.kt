package com.worka.eroyal.feature.report.byarea

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.extensions.percentPrefix
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-28.
 */
class AreaChartItemViewModel(var id: Int?, var visitCount: Int, var totalTask: Int = 100,
                             var areaName: String?, var isSelected: Boolean = false, var cbOnSelected: (Int?, String?) -> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_area_chart

    fun getPercentageChart(): String {
        return String.format("%.0f", (visitCount.toFloat() / totalTask.toFloat()) * 100).percentPrefix()
    }

    fun getSpaceVisibility() : Int? {
        return if (isSelected) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun onClick(){
        cbOnSelected.invoke(id, areaName)
    }
}
