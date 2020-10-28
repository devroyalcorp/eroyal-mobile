package com.worka.eroyal.feature.mycustomers.promo

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.worka.eroyal.R
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-24.
 */
class PromoItemViewModel(var imageUrl: String, var cbOnselected: (String) -> Unit) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_promo

    fun onSelected() {
        cbOnselected.invoke(imageUrl)
    }

    fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels - ((displayMetrics.widthPixels/100) * 50)
    }
}
