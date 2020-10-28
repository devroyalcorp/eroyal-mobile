package com.worka.eroyal.feature.orders

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 28/06/20.
 */
class ProductItemViewModel(
    var id: Int?,
    var productType: String?,
    var productName: String?,
    var qty: String?,
    var cbOnremove: (Int?) -> Unit
) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_product

    fun onRemove() {
        cbOnremove.invoke(id)
    }

    fun getxQty(): String {
        return "x".plus(qty)
    }
}
