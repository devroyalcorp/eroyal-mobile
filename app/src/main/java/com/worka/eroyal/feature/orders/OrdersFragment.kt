package com.worka.eroyal.feature.orders

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentOrdersBinding
import com.worka.eroyal.extensions.getInitialName
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-26.
 */
class OrdersFragment : BaseFragment() {

    private lateinit var binding: FragmentOrdersBinding

    private val viewModel: OrdersViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.toolbar.tvTitle.text = context?.getString(R.string.sales_order)
        binding.toolbar.avatarUrl = viewModel.user.imageProfile
        binding.toolbar.initial = viewModel.user.name.getInitialName()

        viewModel.titlePage.observe(this, Observer {
            binding.toolbar.tvTitle.text = it
            if (it.equals(getString(R.string.summary_order))) {
                binding.toolbar.btnDone.visibility = View.VISIBLE
                binding.toolbar.btnBack.visibility = View.GONE
            }
        })

        viewModel.navController = Navigation.findNavController(context as Activity, R.id.nav_orders)

        with(binding) {
            toolbar.btnBack.setOnClickListener {
                onBackPressed()
            }

            toolbar.btnDone.setOnClickListener {
                ActivityController.finishToLeft(mActivity)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    fun onBackPressed() {
        context?.let { ctx ->
            if (viewModel.navController.currentDestination?.id != R.id.summaryOrdersFragment) {
                if (!viewModel.navController.popBackStack()) {
                    CustomInfoDialog(
                        ctx,
                        ctx.getString(R.string.are_you_sure_discard_all_order_data),
                        onDismiss = {
                            mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            finishToRight()
                        })
                }
            }
        }
    }

}
