package com.worka.eroyal.feature.mycustomers.market

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.worka.eroyal.R
import com.worka.eroyal.data.mycustomer.BrandMarket
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.marketPercentPrefix
import com.worka.eroyal.extensions.toPriceInt
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-07.
 */
class MarketChartViewModel(var brandName: String?, var percentage: String?, var competitors: ArrayList<BrandMarket>, var colors: ArrayList<Int>): SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_market_chart

    fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        return displayMetrics.widthPixels - ((displayMetrics.widthPixels/100) * 75)
    }

    fun getTextColor(): Int {
        return colors[0]
    }
    fun getCompetitorNameAdapter(): GenericAppAdapter<CompetitorNameViewModel> {
        val competitorList = arrayListOf<CompetitorNameViewModel>()
        competitors.forEachIndexed {index, competitor ->
            competitorList.add(CompetitorNameViewModel(competitor.name?.plus(" ").plus(competitor.percentage.marketPercentPrefix()), colors[index + 1]))
        }
        return GenericAppAdapter(competitorList)
    }


    fun getPrices(): ArrayList<Int> {
        val list = arrayListOf<Int>()
        list.add(percentage.toPriceInt())
        competitors.forEach {
            list.add(it.percentage.toPriceInt())
        }
        return list
    }

}
