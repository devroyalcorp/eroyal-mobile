package com.worka.eroyal.feature.tasks

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.component.LongLoadingDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentMarketshareListBinding
import com.worka.eroyal.feature.common.GenericAppAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-10.
 */
class MarketShareListFragment : BaseFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()
    private lateinit var binding: FragmentMarketshareListBinding
    private var adapter: GenericAppAdapter<MarketShareItemViewModel>? = null
    private val longLoading: Dialog? by lazy { LongLoadingDialog.longLoading(context)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_marketshare_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.btnFlyout.setOnClickListener {
            longLoading?.show()
            viewModel.checkout({
                longLoading?.dismiss()
                context?.let { ctx ->
                    mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    CustomInfoDialog(ctx, ctx.getString(R.string.checkout_successful), withoutCancel = true, onDismiss = {
                        mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        ActivityController.finishToLeft(mActivity)
                    })
                }
            }, {
                longLoading?.dismiss()
                context?.let { ctx -> CustomInfoDialog(ctx, it, withoutCancel = true) }
            })
        }
        binding.btnAddMarketShare.setOnClickListener {
            viewModel.navController.navigate(R.id.action_marketShareListFragment_to_addEditMarketShareFragment)
        }

        setupMarketShareList()

        viewModel.marketShareList.observeForever {
            adapter?.list = viewModel.getMarkerShareList()
            adapter?.notifyDataSetChanged()
        }
    }

    fun setupMarketShareList() {
        adapter = GenericAppAdapter(viewModel.getMarkerShareList())
        binding.rvAddMarketShare.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvAddMarketShare.adapter = adapter

        if (viewModel.getMarkerShareList().isEmpty()) {
            mActivity.showLoading()
            viewModel.getExistingMarketShareCustomerList({
                mActivity.hideLoading()
            }, {
                mActivity.hideLoading()
            })
        }
    }
}
