package com.worka.eroyal.feature.report

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/06/20.
 */
class OptionItemViewModel(var type: String?, var name: String?, var cbOnSelected:(String?) -> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_option_list

    fun onSelect() {
        cbOnSelected.invoke(type)
    }
}

