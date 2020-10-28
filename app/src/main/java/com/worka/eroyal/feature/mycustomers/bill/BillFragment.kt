package com.worka.eroyal.feature.mycustomers.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paginate.Paginate
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.databinding.FragmentBillBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/04/20.
 */
class BillFragment : BaseFragment(), Paginate.Callbacks {

    private val viewModel: MyCustomerViewModel by sharedViewModel()

    private lateinit var binding: FragmentBillBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bill, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastOutStandingOrderPage = false
            viewModel.outStandingOrderPage = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupTable()

        viewModel.outStandingOrderViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })
        getData()
    }

    private fun getData() {
        viewModel.isOutStandingOrderLoading = true
        viewModel.getRemainingBill()
        viewModel.getOutStandingOrder {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    private fun setupTable() {
        binding.rvBill.layoutManager = GridLayoutManager(context, Constants.OUT_STANDING_ORDER_COLUMN_SIZE)
        binding.rvBill.adapter = adapter

        Paginate.with(binding.rvBill, this)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastOutStandingOrderPage
    override fun isLoading(): Boolean = viewModel.isOutStandingOrderLoading
    override fun onLoadMore() {
        getData()
    }
}
