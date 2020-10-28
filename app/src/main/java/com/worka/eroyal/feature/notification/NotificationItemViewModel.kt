package com.worka.eroyal.feature.notification

import androidx.databinding.ObservableField
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */
class NotificationItemViewModel(var id: Int?, var read: ObservableField<Boolean?>, var title: String?, var content: String?, var imgUrl: String?, var cbOnClick:(Int?)-> Unit): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_notification

    fun onClicked(){
        read.set(true)
        cbOnClick.invoke(id)
    }
}