package com.worka.eroyal.feature.mycustomers

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-13.
 */
class CustomerItemViewModel(var id: Int, var count: String? = "", var imageAvatar: String? = null, var customerName: String?, var address: String?, var state: String?,
                            var arrowVisibility: Int = View.VISIBLE, var cbOnSelected:(Int) -> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_customers

    fun getStateIcon() : Int {
        return when (state) {
            "active" -> R.drawable.ic_crown_active
            else -> R.drawable.ic_crown_inactive
        }
    }

    fun onSelected(){
        cbOnSelected.invoke(id)
    }
}
