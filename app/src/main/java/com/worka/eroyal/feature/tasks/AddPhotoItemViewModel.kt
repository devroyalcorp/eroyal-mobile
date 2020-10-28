package com.worka.eroyal.feature.tasks

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-10.
 */
class AddPhotoItemViewModel(var onAddPhoto: () -> Unit) : SimpleViewModel {

    override fun layoutId(): Int = R.layout.item_add_photo

    fun onAddPhoto(){
        onAddPhoto.invoke()
    }

}