package com.worka.eroyal.feature.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.databinding.ItemSuggestionBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-27.
 */
class MarketShareSuggestionAdapter(context: Context, var resourceId: Int = R.layout.item_suggestion,
                                   var brands: MutableList<Brand>) : ArrayAdapter<Brand>(context, resourceId) {
    private lateinit var binding: ItemSuggestionBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), resourceId, parent, false)
        binding.text = brands[position].name
        return binding.root
    }

    override fun getItem(position: Int): Brand? {
        return brands[position]
    }

    override fun getCount(): Int {
        return brands.size
    }

}
