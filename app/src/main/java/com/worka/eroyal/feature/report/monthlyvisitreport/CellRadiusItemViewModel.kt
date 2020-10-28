package com.worka.eroyal.feature.report.monthlyvisitreport

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/04/20.
 */
class CellRadiusItemViewModel(var value: String?, var isRight: Boolean): SimpleViewModel {
    override fun layoutId(): Int = if (isRight) R.layout.item_cell_right_radius else R.layout.item_cell_left_radius
}
