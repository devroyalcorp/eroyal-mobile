package com.worka.eroyal.feature.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.databinding.ItemSuggestionBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 29/06/20.
 */
class CustomerSuggestionAdapter(context: Context, var resourceId: Int = R.layout.item_suggestion,
                               var list: MutableList<Customer>) : ArrayAdapter<Customer>(context, resourceId) {
    private lateinit var binding: ItemSuggestionBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), resourceId, parent, false)
        binding.text = list[position].name
        return binding.root
    }

    override fun getItem(position: Int): Customer? {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

}
