package com.worka.eroyal.feature.orders

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
import com.worka.eroyal.component.OptionListDialog
import com.worka.eroyal.databinding.FragmentAddItemOrdersBinding
import com.worka.eroyal.feature.common.ProductSuggestionAdapter
import com.worka.eroyal.feature.report.OptionItemViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 27/06/20.
 */
class AddItemOrdersFragment : BaseFragment() {

    private lateinit var binding: FragmentAddItemOrdersBinding

    private val viewModel: OrdersViewModel by sharedViewModel()

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private val productTypeOptionDialog: OptionListDialog? by lazy {
        val productTypeList = arrayListOf<OptionItemViewModel>()
        resources.getStringArray(R.array.product_type).toList().forEach {
            productTypeList.add(OptionItemViewModel(it, it) {
                viewModel.productType.set(it)
                productTypeOptionDialog?.dismiss()
            })
        }
        context?.let { ctx ->
            OptionListDialog(ctx, ctx.getString(R.string.product_type), productTypeList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_item_orders, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        handler = Handler()
        with(binding) {
            with(this@AddItemOrdersFragment.viewModel) {
                btnCancel.setOnClickListener {
                    resetAddItemForm()
                    navController.popBackStack()
                }

                btnSubmitAddItem.setOnClickListener {
                    if (isValidAddProduct()) {
                        mActivity.hideKeyboard()
                        addProductToList()
                        navController.popBackStack()
                    }
                }

                etJenis.setOnClickListener {
                    productTypeOptionDialog?.show()
                }

                autoProduct.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) {
                            if (autoProduct.isFocused) {
                                productSelected.set(null)
                            }
                            runnable?.let { handler?.removeCallbacks(it) }
                            runnable = Runnable {
                                progressProduct.visibility = View.VISIBLE
                                searchProduct(s.toString())
                                mActivity.hideKeyboard()
                            }
                            handler?.postDelayed(runnable, 800)
                        }
                    }
                })

                productSuggestionList.observeForever {
                    context?.let { it1 ->
                        progressProduct.visibility = View.GONE
                        val adapter = ProductSuggestionAdapter(it1, list = it)
                        autoProduct.setAdapter(adapter)
                        if (autoProduct.isFocused) {
                            autoProduct.showDropDown()
                        }
                    }
                }
                autoProduct.onItemClickListener =
                    object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            handler?.removeCallbacks(runnable)
                            val list = productSuggestionList.value
                            productSelected.set(list?.get(position))
                        }
                    }
            }
        }
    }
}
