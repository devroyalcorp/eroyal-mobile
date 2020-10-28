package com.worka.eroyal.feature.tasks.checklist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentVisitFormBinding
import com.worka.eroyal.extensions.visitPrefix
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.tasks.TasksViewModel
import com.worka.eroyal.utils.ImagePickerHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-09.
 */
class VisitFormFragment : BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()

    lateinit var binding: FragmentVisitFormBinding
    private var adapter: GenericAppAdapter<MediaMarketShareViewModel>? = null

    private val easyImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_visit_form, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.title.value = viewModel.selectedTask.get()?.customer?.name.orEmpty().visitPrefix()
        binding.btnNext.setOnClickListener {
            viewModel.navController.navigate(R.id.marketShareListFragment)
        }

        setupList()
    }

    private fun setupList() {
        with(viewModel) {
            binding.rvCardMediaMarketshare.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setMediaTypeMarketShareData(context, { mediaId, brand ->
                mediaModifyIdTemp = mediaId
                brandModifyTemp = brand
                context?.let {
                    ImagePickerHelper.openCameraFromFragment(it, mActivity, this@VisitFormFragment, easyImage)
                }
            }, {
                context?.let { ctx ->
                    CustomInfoDialog(ctx, ctx.getString(R.string.please_select_correct_brand))
                }
            }) {
               mActivity.hideKeyboard()
            }
            adapter = mediaList.value?.let { GenericAppAdapter(it.toList()) }
            binding.rvCardMediaMarketshare.adapter = adapter

            mediaList.observeForever {
                adapter?.list = it
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    viewModel.updateBrandImage(uri = Uri.fromFile(imageFiles[0].file))
                   /* if ((imageFiles[0].file.length() / 1024) < 1024) {
                        viewModel.updateBrandImage(uri = Uri.fromFile(imageFiles[0].file))
                    } else {
                        context?.let { ctx ->
                            CustomInfoDialog(ctx, ctx.getString(R.string.image_is_too_large))
                        }
                    }*/
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    error.printStackTrace()
                }

                override fun onCanceled(source: MediaSource) {}
            })
    }
}
