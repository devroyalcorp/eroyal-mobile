package com.worka.eroyal.feature.tasks

import android.net.Uri
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-10.
 */
class VisitImageItemViewModel(var imgUrl: Uri?, var onDelete: (Uri?) -> Unit) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_visit_image

    fun onDelete(){
        onDelete.invoke(imgUrl)
    }
}