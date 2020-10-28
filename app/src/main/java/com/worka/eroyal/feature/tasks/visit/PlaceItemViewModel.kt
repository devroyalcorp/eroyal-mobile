package com.worka.eroyal.feature.tasks.visit

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-25.
 */
class PlaceItemViewModel(var id: Int?, var name: String?, var cbOnSelected:(Int?, String?) -> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_place

    fun onSelected(){
        cbOnSelected.invoke(id, name)
    }
}