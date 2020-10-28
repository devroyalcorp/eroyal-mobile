package com.worka.eroyal.feature.tasks.dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.worka.eroyal.R
import com.worka.eroyal.data.visits.Place
import com.worka.eroyal.databinding.LayoutPlaceListDialogBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.tasks.visit.PlaceItemViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-25.
 */
class PlaceListDialog <out T: Place>(context: Context, placeList: ArrayList<T>?, var cbOnSelected:(Int?, String?) -> Unit): BottomSheetDialog(context, R.style.PlaceBottomSheetDialog){


    init {
        val binding: LayoutPlaceListDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_place_list_dialog, null, false)
        val list = arrayListOf<PlaceItemViewModel>()
        placeList?.forEach {
            list.add(PlaceItemViewModel(it.id, it.name){ id, name ->
                cbOnSelected.invoke(id,name)
                dismiss()
            })
        }
        val adapter = GenericAppAdapter(list)

        setContentView(binding.root)
        window?.attributes?.windowAnimations = R.style.DialogAnimationBounce

        binding.rvPlaces.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPlaces.adapter = adapter
        binding.btnClose.setOnClickListener { dismiss() }

    }

}