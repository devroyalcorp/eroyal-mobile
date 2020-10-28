package com.worka.eroyal.feature.orders

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.DatePickerDialog
import com.worka.eroyal.databinding.FragmentSalesOrderFormBinding
import com.worka.eroyal.feature.common.CustomerSuggestionAdapter
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.utils.DateUtils.DD_MMMM_YYYY
import com.worka.eroyal.utils.ImagePickerHelper
import com.worka.eroyal.utils.getDateFormat
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.util.*

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/06/20.
 */
class SalesOrderFormFragment : BaseFragment() {

    private lateinit var binding: FragmentSalesOrderFormBinding

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var adapter: GenericAppAdapter<ProductItemViewModel>? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private val easyImage: EasyImage? by lazy {
        context?.let { EasyImage.Builder(it).setFolderName("eRoyal").build() }
    }

    private val datePickerDialog: DatePickerDialog? by lazy {
        context?.let { ctx ->
            val minDate = Calendar.getInstance().time
            DatePickerDialog(ctx, minDate = minDate.time) { calendar ->
                viewModel.deliveryDate.set(calendar.getDateFormat(DD_MMMM_YYYY))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_order_form, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.viewModel = viewModel
        handler = Handler()

        with(binding) {
            with(this@SalesOrderFormFragment.viewModel) {
                etDeliveryDate.setOnClickListener {
                    datePickerDialog?.show()
                }

                btnAddImage.setOnClickListener {
                    context?.let {
                        ImagePickerHelper.openCameraFromFragment(
                            it,
                            mActivity,
                            this@SalesOrderFormFragment,
                            easyImage
                        )
                    }
                }


                btnAddItem.setOnClickListener {
                    navController.run {
                        if (currentDestination?.id != R.id.addItemOrdersFragment) {
                            navigate(R.id.action_salesOrderFormFragment_to_addItemOrdersFragment)
                        }
                    }
                }

                btnOrder.setOnClickListener {
                    context?.let { ctx ->
                        if (isValidSubmitOrder()) {
                            mActivity.showLoading()
                            submitOrder({
                                mActivity.hideLoading()
                                this@SalesOrderFormFragment.viewModel.navController.navigate(R.id.action_salesOrderFormFragment_to_summaryOrdersFragment)
                            }, {
                                mActivity.hideLoading()
                                CustomInfoDialog(ctx, it, withoutCancel = true)
                            })
                        }
                    }
                }
            }
        }

        setupList()

        with(viewModel) {
            productList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                adapter?.list = viewModel.getProductList()
                adapter?.notifyDataSetChanged()
            })

            context?.let { ctx ->
                onErrorImageOrder.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    CustomInfoDialog(ctx, ctx.getString(R.string.order_image_is_required), withoutCancel = true)
                })

                onErrorOrdersItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    CustomInfoDialog(ctx, ctx.getString(R.string.order_item_is_required), withoutCancel = true)
                })
            }

        }



        setupAutoCompleteToko()
        setupAutoCompleteShipTo()

    }

    private fun setupList() {
        adapter = GenericAppAdapter(arrayListOf())
        binding.rvSalesOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvSalesOrder.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    private fun setupAutoCompleteToko() {
        with(binding) {
            with(this@SalesOrderFormFragment.viewModel) {
                autoToko.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) {
                            if (autoToko.isFocused) {
                                customerSelected.set(null)
                            }
                            runnable?.let { handler?.removeCallbacks(it) }
                            runnable = Runnable {
                                progressToko.visibility = View.VISIBLE
                                searchCustomers(s.toString())
                                mActivity.hideKeyboard()
                            }
                            handler?.postDelayed(runnable, 800)
                        }
                    }
                })

                tokoSuggestionList.observeForever {
                    context?.let { it1 ->
                        progressToko.visibility = View.GONE
                        it?.let {
                            val adapter = CustomerSuggestionAdapter(it1, list = it)
                            autoToko.setAdapter(adapter)
                            if (autoToko.isFocused) {
                                autoToko.showDropDown()
                            }
                        }
                    }
                }
                autoToko.onItemClickListener =
                    object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            handler?.removeCallbacks(runnable)
                            val list = tokoSuggestionList.value
                            customerSelected.set(list?.get(position))
                        }
                    }
            }
        }


    }

    private fun setupAutoCompleteShipTo() {
        with(binding) {
            with(this@SalesOrderFormFragment.viewModel) {
                autoShipTo.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) {
                            if (autoShipTo.isFocused) {
                                shipToSelected.set(null)
                            }
                            runnable?.let { handler?.removeCallbacks(it) }
                            runnable = Runnable {
                                progressShipTo.visibility = View.VISIBLE
                                searchCustomers(s.toString())
                            }
                            handler?.postDelayed(runnable, 800)
                        }
                    }
                })

                tokoSuggestionList.observeForever {
                    context?.let { it1 ->
                        progressShipTo.visibility = View.GONE
                        it?.let {
                            val adapter = CustomerSuggestionAdapter(it1, list = it)
                            autoShipTo.setAdapter(adapter)
                            if (autoShipTo.isFocused) {
                                autoShipTo.showDropDown()
                            }
                        }
                    }
                }
                autoShipTo.onItemClickListener =
                    object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            handler?.removeCallbacks(runnable)
                            val list = tokoSuggestionList.value
                            shipToSelected.set(list?.get(position))
                        }
                    }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(requestCode, resultCode, data, mActivity, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                viewModel.setOrderImage(imageFiles[0].file)
            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
                error.printStackTrace()
            }

            override fun onCanceled(source: MediaSource) {}
        })
    }

    override fun onDestroy() {
        mActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

    fun onBackPressed() {
        context?.let { ctx ->
            if (!viewModel.navController.popBackStack()) {
                CustomInfoDialog(
                    ctx,
                    ctx.getString(R.string.are_you_sure_discard_all_order_data),
                    onDismiss = {
                        mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        finishToRight()
                    })
            }
        }
    }
}
