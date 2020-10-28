package com.worka.eroyal.feature.tasks.checklist

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.worka.eroyal.R
import com.worka.eroyal.data.tasks.BrandImage
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-04.
 */
class MediaMarketShareViewModel(
    var context: Context,
    var mediaId:Int = 0,
    var title: String? = "",
    var brandList: ArrayList<BrandImage>,
    var onAddImage: (Int, BrandImage?) -> Unit,
    var onDeleteImage: (Int, BrandImage?, Uri?) -> Unit,
    var onAddNewBrand:() -> Unit) : SimpleViewModel {

    val brandListLiveData = MutableLiveData<ArrayList<BrandImage>>()
    var adapter = GenericAppAdapter(brands)
    var btnAddVisibility = ObservableField<Int>()

    override fun layoutId(): Int = R.layout.item_media_marketshare

    init {
        brandListLiveData.value = brandList
        brandListLiveData.observeForever {
            adapter?.list = brands
            adapter?.notifyDataSetChanged()
        }
        setVisibilityButtonAddBrand()
    }

    private val brands: ArrayList<BrandMarketShareViewModel>
    get() {
        val list = arrayListOf<BrandMarketShareViewModel>()
        brandListLiveData.value?.forEach {
            list.add(
                BrandMarketShareViewModel(
                it,
                { marketShare ->
                    onDeleteBrand(marketShare)
                },
                {
                    onAddImage.invoke(mediaId, it)
                },
                { brandImage, uri ->
                    onDeleteImage.invoke(mediaId, brandImage, uri)
                },
                { index, brandId, brandName ->
                    brandList.find { it.index == index }?.name = brandName
                    brandList.find { it.index == index }?.id = brandId
                    onAddNewBrand.invoke()
                })
            )
        }
        return list
    }

    fun onAddBrand() {
        val list = brandListLiveData.value
        list?.add(BrandImage().apply { index = System.currentTimeMillis().toString() })
        brandListLiveData.value = list
        list?.let { brandList = it }
        setVisibilityButtonAddBrand()
    }

    fun onDeleteBrand(marketShareImage: BrandImage?){
        val list = brandListLiveData.value
        list?.remove(marketShareImage)
        brandListLiveData.value = list
        list?.let { brandList = it }
        setVisibilityButtonAddBrand()
    }

    private fun setVisibilityButtonAddBrand() {
        if (brandList.size < 5){
            btnAddVisibility.set(View.VISIBLE)
        } else {
            btnAddVisibility.set(View.INVISIBLE)
        }
    }
}
