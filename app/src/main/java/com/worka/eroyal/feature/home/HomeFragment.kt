package com.worka.eroyal.feature.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants.FLOW_TYPE
import com.worka.eroyal.base.Constants.TASK
import com.worka.eroyal.base.Constants.VISIT
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentHomeBinding
import com.worka.eroyal.extensions.appendColor
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.getInitialName
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.main.MainActivity
import com.worka.eroyal.feature.main.MainViewModel
import com.worka.eroyal.feature.orders.OrdersActivity
import com.worka.eroyal.feature.tasks.TasksActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment() {
    private lateinit var adapter : GenericAppAdapter<SimpleViewModel>
    private lateinit var activityAdapter : GenericAppAdapter<SimpleViewModel>
    private lateinit var binding : FragmentHomeBinding
    private val viewModel: HomeViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.btnBack.visibility = View.GONE
        binding.toolbar.tvTitle.visibility = View.GONE

        viewModel.setGreating()
        binding.shimmerHome.startShimmer()

        viewModel.taskRemaining.observeForever {
           binding.tvTaskRemaining.text = getAppContext().resources.getString(R.string.task_remaining, it).appendColor(it.toString())
        }

        binding.srHome.setOnRefreshListener {
            binding.srHome.isRefreshing = true
            getData()
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    fun getData(){
        binding.apply {
            shimmerHome.startShimmer()
            shimmerHome.visibility = View.VISIBLE
            layoutHome.visibility = View.GONE

            Handler().postDelayed({
                viewModel?.getHomeData({
                    shimmerHome.stopShimmer()
                    srHome.isRefreshing = false
                    shimmerHome.visibility = View.GONE
                    layoutHome.visibility = View.VISIBLE
                    tvError.visibility = View.GONE
                    setupMenu()
                }, {
                    shimmerHome.visibility = View.GONE
                    layoutHome.visibility = View.GONE
                    tvError.text = it
                    tvError.visibility = View.VISIBLE
                    srHome.isRefreshing = false
                    shimmerHome.stopShimmer()

                })
            }, 500)
        }
    }

    fun setupMenu() {
        adapter = GenericAppAdapter(viewModel.getHomeMenu {
            when (it) {
                getString(R.string.tasks) -> ActivityController.navigateToRight(activity, TasksActivity::class.java, false, Bundle().apply {
                        putString(FLOW_TYPE, TASK)
                    })

                getString(R.string.me) -> mainViewModel.onNavigateToMePage.invoke()
                getString(R.string.visits) -> ActivityController.navigateToRight(activity, TasksActivity::class.java, false, Bundle().apply {
                        putString(FLOW_TYPE, VISIT)
                    })
                getString(R.string.orders) -> context?.let { ctx ->
                    ActivityController.navigateToRight(activity, OrdersActivity::class.java, false, null)
                }
            }
        })
        activity?.let { act ->
            if (!act.isDestroyed)
                (act as MainActivity).setupDrawer()
        }
        binding.rvHome.layoutManager = GridLayoutManager(context, 2)
        binding.rvHome.adapter = adapter

        activityAdapter = GenericAppAdapter(viewModel.activityList)
        binding.rvActivity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvActivity.adapter = activityAdapter

    }
}
