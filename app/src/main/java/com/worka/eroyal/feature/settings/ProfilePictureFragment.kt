package com.worka.eroyal.feature.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentProfilePictureBinding
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-22.
 */
class ProfilePictureFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by sharedViewModel()

    private lateinit var binding: FragmentProfilePictureBinding

    private val easyImage: EasyImage? by lazy {
        context?.let {
            EasyImage.Builder(it).setFolderName("eRoyal").build()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_picture, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.imageProfile.set(Uri.parse(viewModel.user.imageProfile))

        binding.btnNewProfilePicture.setOnClickListener {
            context?.let { ctx -> mActivity.requestForPermissions(
                Constants.CAMERA_STORAGE_PERMISSION,
                Constants.REQUEST_CAMERA, grantResults = { _, permissionResult ->
                    if (permissionResult == PermissionResult.GRANTED){
                        ImagePickerDialog.buildDialog(ctx, {
                            easyImage?.openCameraForImage(this)
                        }, {
                            easyImage?.openGallery(this)
                        })?.show()
                    } else {
                        CustomInfoDialog(ctx, getString(R.string.storage_camera_permission_required))
                    }
                })

            }
        }

        binding.btnDeleteProfilePicture.setOnClickListener {
            mActivity.showLoading()
            viewModel.deleteProfileImage({
                mActivity.hideLoading()
            },{
                mActivity.hideLoading()
                context?.let { ctx -> CustomInfoDialog(ctx, it) }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                viewModel.imageProfile.set(result.uri)
                viewModel.userImageProfile.set(result.uri.toString())
                sendProfileImage()
            }
        } else {
            easyImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
                    override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                        context?.let {
                            CropImage.activity(Uri.fromFile(imageFiles[0].file))
                                .setAspectRatio(1,1)
                                .start(it, this@ProfilePictureFragment)
                        }
                    }

                    override fun onImagePickerError(error: Throwable, source: MediaSource) {
                        error.printStackTrace()
                    }

                    override fun onCanceled(source: MediaSource) {}
                })
        }
    }

    fun sendProfileImage() {
        mActivity.showLoading()
        context?.let { ctx ->
            viewModel.updateProfileImage(ctx, { user ->
                EventBus.getDefault().post(user)
                CustomInfoDialog(ctx, ctx.getString(R.string.image_profile_updated))
                mActivity.hideLoading()
            }, {
                mActivity.hideLoading()
                CustomInfoDialog(ctx, it)
            })
        }
    }
}
