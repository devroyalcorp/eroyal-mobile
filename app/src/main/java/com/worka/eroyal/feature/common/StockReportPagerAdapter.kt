package com.worka.eroyal.feature.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.worka.eroyal.R
import com.worka.eroyal.data.report.Stock
import com.worka.eroyal.databinding.ItemStockReportBinding
import com.worka.eroyal.feature.mycustomers.bill.CellItemViewModel
import com.worka.eroyal.feature.mycustomers.bill.HeaderColumnItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 02/07/20.
 */
class StockReportPagerAdapter(var data: ArrayList<Stock>): PagerAdapter() {

    private lateinit var binding: ItemStockReportBinding

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.item_stock_report, container, false)
        binding.data = data[position]

        val list = arrayListOf<SimpleViewModel>()
        list.add(HeaderColumnItemViewModel(container.context.getString(R.string.mattress)))
        list.add(CellItemViewModel(data[position].mattress.toString()))
        list.add(HeaderColumnItemViewModel(container.context.getString(R.string.divan)))
        list.add(CellItemViewModel(data[position].divan.toString()))
        list.add(HeaderColumnItemViewModel(container.context.getString(R.string.headboard)))
        list.add(CellItemViewModel(data[position].headboard.toString()))
        list.add(HeaderColumnItemViewModel(container.context.getString(R.string.sorong)))
        list.add(CellItemViewModel(data[position].sorong.toString()))

        val adapter = GenericAppAdapter(list)
        binding.adapter = adapter

        container.addView(binding.root)
        return binding.root
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
