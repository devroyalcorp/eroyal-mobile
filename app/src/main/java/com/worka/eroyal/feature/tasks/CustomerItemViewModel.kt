package com.worka.eroyal.feature.tasks

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

class CustomerItemViewModel(var id: Int, var customertName: String?, var address: String? = "", var cbOnSelected:(Int) -> Unit) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_customer

    fun onSelected(){
        cbOnSelected.invoke(id)
    }
}