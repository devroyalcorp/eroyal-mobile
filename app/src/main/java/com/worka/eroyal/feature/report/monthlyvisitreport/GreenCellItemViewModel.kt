package com.worka.eroyal.feature.report.monthlyvisitreport

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/04/20.
 */
class GreenCellItemViewModel(var value: String?): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_green_cell
}
