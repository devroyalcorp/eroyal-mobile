package com.worka.eroyal.feature.tasks

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-16.
 */

class MarketShareItemViewModel(
    var id: String? = null,
    var position: String,
    var name: String?,
    var price: String?,
    var onEdit: (String) -> Unit,
    var onRemoved: (String) -> Unit
) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_market_share

    fun getVisibilityEdit(): Int {
       return id?.let {
            View.VISIBLE
        }?: run {
            View.GONE
        }
    }

    fun onRemove() {
        onRemoved.invoke(position.replace(".", "").replace(" ", ""))
    }

    fun onEdit() {
        onEdit.invoke(position.replace(".", "").replace(" ", ""))
    }
}
