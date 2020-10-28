package com.worka.eroyal.feature.tasks.visit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.data.visits.Branch
import com.worka.eroyal.data.visits.Place
import com.worka.eroyal.databinding.FragmentAddCustomerBinding
import com.worka.eroyal.feature.common.MarketShareSuggestionAdapter
import com.worka.eroyal.feature.tasks.TasksViewModel
import com.worka.eroyal.feature.tasks.dialog.PlaceListDialog
import com.worka.eroyal.utils.ImagePickerHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-24.
 */
class AddCustomerFragment : BaseFragment() {

    private val viewModel: TasksViewModel by sharedViewModel()

    lateinit var binding: FragmentAddCustomerBinding
    var customerAreaDialog: PlaceListDialog<Place>? = null
    var branchDialog: PlaceListDialog<Branch>? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val easyImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    private val easyVisitImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_customer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        mActivity.showLoading()
        handler = Handler()
        viewModel.getAreaList {
            mActivity.hideLoading()
            customerAreaDialog = context?.let {
                PlaceListDialog(it, viewModel.areas) { id, name ->
                    viewModel.areaId = id
                    binding.etCustomerArea.setText(name)
                    getBranches()
                }
            }
        }

        with(binding) {

            btnAddPhotoAddCustomer.setOnClickListener {
                context?.let {
                    ImagePickerHelper.openCameraFromFragment(it, mActivity, this@AddCustomerFragment, easyImage)
                }
            }
            etCustomerName.addTextChangedListener(OnTextChanged())
            etCustomerCity.addTextChangedListener(OnTextChanged())
            etCustomerAddress.addTextChangedListener(OnTextChanged())
            etCustomerType.addTextChangedListener(OnTextChanged())
            etCustomerContactPerson.addTextChangedListener(OnTextChanged())
            etCustomerPhone.addTextChangedListener(OnTextChanged())
            etVisitReason.addTextChangedListener(OnTextChanged())
            autoBrand.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        if (autoBrand.isFocused) {
                            viewModel?.selectedMarketShareTemp?.set(null)
                        }
                        runnable?.let { handler?.removeCallbacks(it) }
                        runnable = Runnable {
                            brandProgress.visibility = View.VISIBLE
                            viewModel?.searchMarketShare(s.toString(), Constants.INTERNAL_SCOPE)
                        }
                        handler?.postDelayed(runnable, 800)
                    }
                }
            })

            viewModel?.marketShareSuggestionList?.observeForever { list ->
                context?.let { ctx ->
                    brandProgress.visibility = View.GONE
                    if (!list.isNullOrEmpty()) {
                        val adapter = MarketShareSuggestionAdapter(ctx, brands = list)
                        autoBrand.setAdapter(adapter)
                        if (autoBrand.isFocused) {
                            autoBrand.showDropDown()
                        }
                    }
                }
            }
            autoBrand.onItemClickListener =
                object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        handler?.removeCallbacks(runnable)
                        val list = viewModel?.marketShareSuggestionList?.value
                        viewModel?.selectedMarketShareTemp?.set(list?.get(position))
                    }
                }

            etCustomerArea.setOnClickListener { customerAreaDialog?.show() }
            etCustomerBranch.setOnClickListener { branchDialog?.show() }
            btnAddPhotoFollowUpVisit.setOnClickListener {
                context?.let {
                    ImagePickerHelper.openCameraFromFragment(it, mActivity, this@AddCustomerFragment, easyVisitImage)
                }
            }

            btnPickLocation.setOnClickListener { viewModel?.navController?.navigate(R.id.action_addCustomerFragment_to_placePickerFragment) }

            btnSubmitAddCustomer.setOnClickListener {
                mActivity.showLoading()
                context?.let { ctx ->
                    viewModel?.addCustomer(ctx, {
                        checkInFollowUpVisit()
                    }, {
                        mActivity.hideLoading()
                        CustomInfoDialog(ctx, it)
                    })
                }
            }
        }

        viewModel.validateButtonAddCustomer()
    }

    private fun getBranches(){
        viewModel.getBranchList {
            mActivity.hideLoading()
            branchDialog = context?.let {
                PlaceListDialog(it, viewModel.branches) { id, name ->
                    viewModel.branchId = id
                    binding.etCustomerBranch.setText(name)
                }
            }
        }
    }

    private fun checkInFollowUpVisit(){
        viewModel.checkInFollowUpVisit(context, {
            mActivity.hideLoading()
            viewModel.navController.navigate(R.id.action_addCustomerFragment_to_signatureFormFragment)
        }, {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                viewModel.customerImageProfileUri.set(Uri.fromFile(imageFiles[0].file))
                viewModel.validateButtonAddCustomer()
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                error.printStackTrace()
            }

            override fun onCanceled(source: MediaSource) {}
        })

        easyVisitImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                viewModel.urlImageFollowUpVisit.set(Uri.fromFile(imageFiles[0].file))
                viewModel.validateButtonAddCustomer()
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                error.printStackTrace()
            }

            override fun onCanceled(source: MediaSource) {}
        })
    }

    inner class OnTextChanged: TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.validateButtonAddCustomer()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

}
