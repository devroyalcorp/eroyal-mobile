package com.worka.eroyal.feature.tasks

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
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.databinding.FragmentAddEditMarketShareBinding
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.feature.common.MarketShareSuggestionAdapter
import com.worka.eroyal.utils.addThousandSeparator
import com.worka.eroyal.utils.getCleanString
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-22.
 */
class AddEditMarketShareFragment: BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentAddEditMarketShareBinding
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_edit_market_share, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.autoMarketShareName.isEnabled = true
        viewModel.selectedMarketShareForEdit?.let {
            viewModel.selectedMarketShareTemp.set(it)
            binding.autoMarketShareName.setText(it.name)
            binding.autoMarketShareName.isEnabled = false
            binding.etMarketSharePrice.setText(it.price?.toLong()?.formatThousandSeparator())
        }
        handler = Handler()
        binding.autoMarketShareName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    viewModel.selectedMarketShareTemp.set(null)
                    viewModel.validateButtonAddMarketShare(binding.autoMarketShareName.text.toString(), binding.etMarketSharePrice.text.toString())
                    runnable?.let { handler?.removeCallbacks(it) }
                    runnable = Runnable {
                        binding.searchMarketProgress.visibility = View.VISIBLE
                        viewModel.searchMarketShare(s.toString(), "")
                    }
                    handler?.postDelayed(runnable, 800)
                }
            }
        })

        binding.etMarketSharePrice.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                viewModel.validateButtonAddMarketShare(binding.autoMarketShareName.text.toString(), binding.etMarketSharePrice.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        viewModel.marketShareSuggestionList.observeForever { list ->
            context?.let { it1 ->
                binding.searchMarketProgress.visibility = View.GONE
                list?.let {
                    val adapter = MarketShareSuggestionAdapter(it1, brands = it)
                    binding.autoMarketShareName.setAdapter(adapter)
                    binding.autoMarketShareName.showDropDown()
                }
            }
        }

        binding.autoMarketShareName.onItemClickListener = object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    handler?.removeCallbacks(runnable)
                    val list = viewModel.marketShareSuggestionList.value
                    viewModel.selectedMarketShareTemp.set(list?.get(position))
                    binding.etMarketSharePrice.requestFocus()
                }
            }
        binding.etMarketSharePrice.addThousandSeparator {
            val marketShare = viewModel.selectedMarketShareTemp.get()
            marketShare?.price = binding.etMarketSharePrice.text.toString().getCleanString()
            viewModel.selectedMarketShareTemp.set(marketShare)
        }

        binding.btnDone.setOnClickListener {
            mActivity.hideKeyboard()
            val marketShare = Brand().apply {
                viewModel.selectedMarketShareForEdit?.let {
                    id = it.id
                    price = binding.etMarketSharePrice.text.toString().getCleanString()
                } ?: run {
                    price = viewModel.selectedMarketShareTemp.get()?.price
                }
                name = binding.autoMarketShareName.text.toString()
            }
            viewModel.updateMarketShareToList(marketShare)
            binding.autoMarketShareName.setAdapter(null)
            viewModel.navController.popBackStack()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.selectedMarketShareForEdit = null
        binding.autoMarketShareName.addTextChangedListener(null)
        binding.autoMarketShareName.setAdapter(null)
        viewModel.marketShareSuggestionList.value = null
    }
}
