package com.worka.eroyal.feature.tasks

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.worka.eroyal.R
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.common.SimpleViewModel

class TargetProgressItemViewModel(var completedTask: Int?, var totalTask: Int?) : SimpleViewModel{
    override fun layoutId(): Int = R.layout.item_target_progress

    fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels - ((displayMetrics.widthPixels/100) * 20)
    }
}