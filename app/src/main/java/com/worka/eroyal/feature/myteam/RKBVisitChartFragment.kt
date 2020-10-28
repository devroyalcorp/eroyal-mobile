package com.worka.eroyal.feature.myteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentRkbVisitChartBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 11/06/20.
 */
class RKBVisitChartFragment: BaseFragment() {
    private lateinit var binding: FragmentRkbVisitChartBinding

    private val viewModel: MyTeamViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rkb_visit_chart, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
    }
}
