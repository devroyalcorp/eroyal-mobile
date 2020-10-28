package com.worka.eroyal.feature.mycustomers.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentMarketCustomerBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-02-07.
 */
class MarketCustomerFragment : BaseFragment() {
    private val viewModel: MyCustomerViewModel by sharedViewModel()
    private lateinit var binding: FragmentMarketCustomerBinding
    private var adapter: GenericAppAdapter<MarketChartViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_market_customer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getMarketCustomer({
            setupMarketList()
            mActivity.hideLoading()
        },{
            mActivity.hideLoading()
        })
    }

    fun setupMarketList (){
        adapter = GenericAppAdapter(viewModel.marketViewModelList)
        binding.rvMarket.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMarket.adapter = adapter
    }
}
