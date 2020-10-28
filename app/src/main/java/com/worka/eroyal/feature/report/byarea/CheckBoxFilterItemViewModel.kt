package com.worka.eroyal.feature.report.byarea

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-30.
 */

class CheckBoxFilterItemViewModel(var id: Int?, var name: String?, var isSelected: Boolean = true): SimpleViewModel{
    override fun layoutId(): Int = R.layout.item_checkbox_filter
}
