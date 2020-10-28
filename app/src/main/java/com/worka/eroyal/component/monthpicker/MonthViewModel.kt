package com.worka.eroyal.component.monthpicker

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-20.
 */
class MonthViewModel(var month: String?, var year: String?, var cbOnClick: (String?) -> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_month

    fun cbOnClick(){
        this.cbOnClick.invoke(month.plus(" ").plus(year))
    }
}
