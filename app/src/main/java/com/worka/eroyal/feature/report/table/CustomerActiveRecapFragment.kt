package com.worka.eroyal.feature.report.table

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
import com.worka.eroyal.databinding.FragmentCustomerActiveRecapBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 13/04/20.
 */
class CustomerActiveRecapFragment : BaseFragment(), Paginate.Callbacks {

    private val viewModel: ReportViewModel by sharedViewModel()

    private lateinit var binding: FragmentCustomerActiveRecapBinding
    private var adapter: GenericAppAdapter<SimpleViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_customer_active_recap, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        if (adapter == null) {
            adapter = GenericAppAdapter(arrayListOf())
            viewModel.isLastPageCustomerActiveRecap = false
            viewModel.pageCustomerActiveRecap = 1
        } else {
            adapter?.notifyDataSetChanged()
        }
        setupTable()

        viewModel.customerActiveRecapViewModelList.observe(this, Observer {
            adapter?.list = it
            adapter?.notifyDataSetChanged()
        })

        viewModel.selectedMonth.observe(this, Observer {
            viewModel.isLastPageCustomerActiveRecap = false
            viewModel.pageCustomerActiveRecap = 1
            adapter?.list = arrayListOf()
            getData()
        })
    }

    fun getData(){
        viewModel.isLoadingCustomerActiveRecap = true
        viewModel.getCustomerActiveRecap {
            mActivity.hideLoading()
            context?.let { ctx -> CustomInfoDialog(ctx, it) }
        }
    }

    private fun setupTable() {
        binding.rvTableReport.layoutManager = GridLayoutManager(context, Constants.CUSTOMER_ACTIVE_RECAP_COLUMN_SIZE)
        binding.rvTableReport.adapter = adapter

        Paginate.with(binding.rvTableReport, this)
            .setLoadingTriggerThreshold(5)
            .addLoadingListItem(true)
            .build()
    }

    override fun hasLoadedAllItems(): Boolean = viewModel.isLastPageCustomerActiveRecap
    override fun isLoading(): Boolean = viewModel.isLoadingCustomerActiveRecap
    override fun onLoadMore() {
        getData()
    }
}
