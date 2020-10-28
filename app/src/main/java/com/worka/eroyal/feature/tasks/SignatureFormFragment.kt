package com.worka.eroyal.feature.tasks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.gcacace.signaturepad.views.SignaturePad
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentSignatureFormBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import com.worka.eroyal.utils.ImagePickerHelper as ImagePickerHelper1


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-10.
 */
class SignatureFormFragment : BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentSignatureFormBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    private val easyImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signature_form, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.viewModel = viewModel

        setupImageList()

        binding.btnNext.setOnClickListener {
            viewModel.saveBitmap(binding.signaturePad.signatureBitmap)
            viewModel.navController.navigate(R.id.visitFormFragment)
        }

        binding.signaturePad.setOnSignedListener(object :
            SignaturePad.OnSignedListener {

            override fun onStartSigning() {
                binding.btnClearSignature.visibility = View.VISIBLE
            }

            override fun onSigned() {
                viewModel.canNextSignature.set(true)
            }

            override fun onClear() {
                binding.btnClearSignature.visibility = View.INVISIBLE
                viewModel.canNextSignature.set(false)
            }
        })

        binding.btnClearSignature.setOnClickListener {
            binding.signaturePad.clear()
        }

        viewModel.signatureBitmap.get()?.let { binding.signaturePad.signatureBitmap = it }

        viewModel.visitImageUris.observeForever {
            adapter?.list = viewModel.getVisitImageList {
                openCamera()
            }
            adapter?.notifyDataSetChanged()
        }
        viewModel.startTimer(mActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.textTimer.set("")
    }

    override fun onDestroy() {
        viewModel.cancelTimer()
        super.onDestroy()
    }

    fun setupImageList(){
        adapter = GenericAppAdapter(viewModel.getVisitImageList {
            openCamera()
        })
        binding.rvImageVisit.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImageVisit.adapter = adapter
    }

    fun openCamera() {
        context?.let { ImagePickerHelper1.openCameraFromFragment(it, mActivity, this, easyImage) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
               viewModel.addVisitImage(Uri.fromFile(imageFiles[0].file))
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                error.printStackTrace()
            }

            override fun onCanceled(source: MediaSource) {}
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etNotes.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    fun onBackPressed() {
        with(viewModel) {
            if (navController.currentDestination?.id == R.id.signatureFormFragment) {
                if (isBackButtonPressedOnce) {
                    mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    isBackButtonPressedOnce = false
                    goBack()
                } else {
                    isBackButtonPressedOnce = true
                    Toast.makeText(
                        context,
                        resources.getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({ isBackButtonPressedOnce = false }, 1000)
                }
            } else {
                goBack()
            }
        }
    }

    private fun goBack() {
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }
}
