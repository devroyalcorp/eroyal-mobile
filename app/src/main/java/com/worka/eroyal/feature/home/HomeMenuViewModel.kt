package com.worka.eroyal.feature.home

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

class HomeMenuViewModel(
    var iconHomeActive: Int,
    var iconHomeInactive: Int,
    var title: String,
    var isEnabled: Boolean? = true,
    var onClick: (String) -> Unit
) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_home_menu

    fun onClick() {
        isEnabled?.let {
            if (it) {
                onClick.invoke(title)
            }
        }
    }

    fun getIconHome(): Int? {
        return isEnabled?.let {
            if (it) {
                iconHomeActive
            } else {
                iconHomeInactive
            }
        }
    }
}