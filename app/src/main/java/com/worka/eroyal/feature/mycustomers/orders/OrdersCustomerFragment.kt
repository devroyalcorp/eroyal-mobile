package com.worka.eroyal.feature.mycustomers.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentCustomerOrdersBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-24.
 */
class OrdersCustomerFragment: BaseFragment() {
    private lateinit var binding: FragmentCustomerOrdersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_orders, container, false)
        return binding.root
    }
}
