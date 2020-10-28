package com.worka.eroyal.feature.me.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentStatisticMeBinding
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.me.MeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-10.
 */
class StatisticMeFragment: BaseFragment() {
    private val viewModel: MeViewModel by sharedViewModel()
    private lateinit var binding: FragmentStatisticMeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic_me, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        context?.getColor(android.R.color.transparent)?.let { binding.visitChart.setBackgroundColor(it) }

        viewModel.selectedDate.observe(this, androidx.lifecycle.Observer {
            getData()
        })

        getData()
    }

    private fun getData() {
        viewModel.getStatistic({},{})
    }
}
