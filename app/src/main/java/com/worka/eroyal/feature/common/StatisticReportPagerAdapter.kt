package com.worka.eroyal.feature.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.worka.eroyal.R
import com.worka.eroyal.data.report.ChartReport
import com.worka.eroyal.databinding.ItemStatisticReportBinding
import com.worka.eroyal.extensions.getAppContext

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-28.
 */
class StatisticReportPagerAdapter(var data: ArrayList<ChartReport>): PagerAdapter() {

    private lateinit var binding: ItemStatisticReportBinding

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.item_statistic_report, container, false)
        binding.data = data[position]

        binding.tvCurrentLegend.text = getAppContext().getString(R.string.current).plus(" ").plus(
            getAppContext().getString(getLegendText(position)))

        binding.tvPrevLegend.text = getAppContext().getString(R.string.previous).plus(" ").plus(
            getAppContext().getString(getLegendText(position)))

        binding.tvTargetLegend.visibility = getTargetLegendVisibility(position)
        binding.legendTarget.visibility = getTargetLegendVisibility(position)

        container.addView(binding.root)
        return binding.root
    }

    private fun getLegendText(position: Int) : Int {
        return if (data[position].chartTitle.equals("sales", true)){
            R.string.year
        } else {
            R.string.month
        }
    }

    private fun getTargetLegendVisibility(position: Int): Int {
        return if (data[position].target.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
