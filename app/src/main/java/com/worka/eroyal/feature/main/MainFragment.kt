package com.worka.eroyal.feature.main

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.IS_FROM_BOTTOM_BAR
import com.worka.eroyal.base.PermissionResult
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentMainBinding
import com.worka.eroyal.feature.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MainFragment : BaseFragment() {
    private val viewModel: MainViewModel by sharedViewModel()
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(context as Activity, R.id.navHostFragment)
        setupBottomNav()

        if (viewModel.sessionStorage.getAccessToken().isNullOrBlank()) {
            ActivityController.navigateWithClearTop(mActivity, LoginActivity::class.java)
            mActivity.finishAffinity()
        }

        viewModel.onNavigateToMePage.observe(this, Observer {
            Handler().postDelayed({
                binding.bottomNavigation.selectedItemId = R.id.bottomNavigationMeMenuId
            }, 300)
        })

    }

    fun setupBottomNav() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            return@setOnNavigationItemSelectedListener when (item.itemId) {
                R.id.bottomNavigationHomeMenuId -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.bottomNavigationClockMenuId -> {
                    navController.navigate(R.id.clockInOutFragment)
                    true
                }
                R.id.bottomNavigationTasksMenuId -> {
                    requestPermission()
                    true
                }
                R.id.bottomNavigationOrdersMenuId -> {
                    navController.navigate(R.id.ordersFragment)
                    true
                }
                R.id.bottomNavigationMeMenuId -> {
                    navController.navigate(R.id.mePagerFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun requestPermission() {
        mActivity.requestForPermissions(Constants.LOCATION_PERMISSIONS, Constants.REQUEST_LOCATION, grantResults = { _, permissionResult ->
            if (permissionResult == PermissionResult.GRANTED) {
                navController.navigate(R.id.tasksFragment, Bundle().apply {
                    putString(Constants.FLOW_TYPE, Constants.TASK)
                    putBoolean(IS_FROM_BOTTOM_BAR, true)
                })
            } else {
                context?.let { ctx -> CustomInfoDialog(ctx, getString(R.string.location_permission_required), onDismiss = { requestPermission()}) }
            }
        })
    }

}
