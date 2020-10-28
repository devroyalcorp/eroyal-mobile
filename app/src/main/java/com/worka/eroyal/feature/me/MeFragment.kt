package com.worka.eroyal.feature.me

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.databinding.FragmentMeBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.utils.AnimationHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */
class MeFragment : BaseFragment() {
    private val viewModel: MeViewModel by sharedViewModel()
    private lateinit var visitedAdapter: GenericAppAdapter<SimpleViewModel>
    private lateinit var failedAdapter: GenericAppAdapter<SimpleViewModel>
    private lateinit var freeVisitAdapter: GenericAppAdapter<SimpleViewModel>
    private lateinit var brandRevenueAdapter: GenericAppAdapter<SimpleViewModel>
    private lateinit var animationHelper: AnimationHelper
    lateinit var binding: FragmentMeBinding
    private var measureHeightVisitedLayout = 0
    private var measureHeightFailedLayout = 0
    private var measureHeightFreeVisitLayout = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        mActivity.showLoading()
        animationHelper = AnimationHelper(1)
        viewModel.getCustomerTaskList({
            Handler().postDelayed({
                setupList()
            }, 500)
        },{
            mActivity.hideLoading()
        })

        binding.btnExpandVisited.setOnClickListener {
            animateExpand(binding.btnExpandVisited, binding.layoutVisitedList, measureHeightVisitedLayout)
        }

        binding.btnExpandFailed.setOnClickListener {
            animateExpand(binding.btnExpandFailed, binding.layoutFailedList, measureHeightFailedLayout)
        }

        binding.btnExpandFreeVisit.setOnClickListener {
            animateExpand(binding.btnExpandFreeVisit, binding.layoutFreeVisitList, measureHeightFreeVisitLayout)
        }
    }

    fun animateExpand(button: View, layout:View, measureHeight: Int){
        when(layout.visibility){
            View.GONE -> {
                animationHelper.expandLayout(layout, measureHeight)
                button.animate().rotation(0f).setDuration(animationHelper.getDuration()).start()
            }
            else -> {
                measureHeightVisitedLayout = binding.layoutVisitedList.measuredHeight
                measureHeightFailedLayout = binding.layoutFailedList.measuredHeight
                measureHeightFreeVisitLayout = binding.layoutFreeVisitList.measuredHeight
                animationHelper.collapseLayout(layout)
                button.animate().rotation(180f).setDuration(animationHelper.getDuration()).start()
            }
        }
    }

    fun setupList(){
        visitedAdapter = GenericAppAdapter(viewModel.getVisitedData().toList())
        binding.rvCustomerVisited.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCustomerVisited.adapter = visitedAdapter

        failedAdapter = GenericAppAdapter(viewModel.getFailedData().toList())
        binding.rvCustomerFailed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCustomerFailed.adapter = failedAdapter

        freeVisitAdapter = GenericAppAdapter(viewModel.getFreeVisitData().toList())
        binding.rvFreeVisit.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvFreeVisit.adapter = freeVisitAdapter

        brandRevenueAdapter = GenericAppAdapter(viewModel.getRevenueBrandList().toList())
        binding.rvMeHorizontal.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMeHorizontal.adapter = brandRevenueAdapter

        failedAdapter.notifyDataSetChanged()
        visitedAdapter.notifyDataSetChanged()
        freeVisitAdapter.notifyDataSetChanged()
        mActivity.hideLoading()
    }
}
