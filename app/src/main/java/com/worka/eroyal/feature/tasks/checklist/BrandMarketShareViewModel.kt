package com.worka.eroyal.feature.tasks.checklist

import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import com.worka.eroyal.R
import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.data.tasks.BrandImage
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.MarketShareSuggestionAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.tasks.AddPhotoItemViewModel
import com.worka.eroyal.feature.tasks.VisitImageItemViewModel
import com.worka.eroyal.repository.TasksRepository
import org.koin.core.inject

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-04.
 */
class BrandMarketShareViewModel(
    var marketShareImage: BrandImage?,
    var onDeleteBrand: (BrandImage?) -> Unit,
    var onAddImage: (BrandImage?) -> Unit,
    var onDeleteImage: (BrandImage?, Uri?) -> Unit,
    var onSetBrandName:(String?, String?, String?) -> Unit) : SimpleViewModel {

    private val repository: TasksRepository by inject()

    override fun layoutId(): Int = R.layout.item_brand_marketshare
    var marketShareSuggestionList = mutableListOf<Brand>()
    var selectedBrand: Brand? = null
    var autoCompleteAdapter: MarketShareSuggestionAdapter? = MarketShareSuggestionAdapter(getAppContext(), brands = mutableListOf() )
    val imageList: List<SimpleViewModel> by lazy {
        val list = arrayListOf<SimpleViewModel>()
        marketShareImage?.imageList?.forEach {
            list.add(VisitImageItemViewModel(it) {
                onDeleteImage.invoke(marketShareImage, it)
            })
        }

        if (marketShareImage?.imageList.isNullOrEmpty() || list.size < 5) {
            list.add(AddPhotoItemViewModel {
                onAddImage.invoke(marketShareImage)
            })
        }
        return@lazy list
    }
    var adapter = GenericAppAdapter(imageList)

    val handler = Handler()
    var runnable: Runnable? = null

    val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    runnable?.let { it -> handler.removeCallbacks(it) }
                    runnable = Runnable {
                        repository.searchMarketShare(s.toString(), Constants.INTERNAL_SCOPE, {
                            it?.brands?.toMutableList()?.let { list ->
                                if (list.isNotEmpty()) {
                                    autoCompleteAdapter?.brands = list
                                    marketShareSuggestionList = list
                                    autoCompleteAdapter?.notifyDataSetChanged()
                                }
                            }
                        },{})
                    }
                    runnable?.let { it -> handler.postDelayed(it, 800)}
                }
            }
        }

    }
    val adapterItemClick: AdapterView.OnItemClickListener by lazy {
        object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                runnable?.let { it ->  handler.removeCallbacks(it) }
                selectedBrand = marketShareSuggestionList[position]
                marketShareImage?.id = selectedBrand?.id
                marketShareImage?.name = selectedBrand?.name
                onSetBrandName.invoke(marketShareImage?.index, selectedBrand?.id, selectedBrand?.name)
            }
        }
    }

    fun onDeleteBrand(){
        onDeleteBrand.invoke(marketShareImage)
    }

}
