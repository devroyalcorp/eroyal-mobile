package com.worka.eroyal.feature.settings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.firebase.iid.FirebaseInstanceId
import com.worka.eroyal.BuildConfig
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentSettingsMenuBinding
import com.worka.eroyal.feature.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */

class SettingsMenuFragment : BaseFragment() {
    private val viewModel: SettingsViewModel by sharedViewModel()
    private lateinit var binding: FragmentSettingsMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_menu, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.navController = Navigation.findNavController(context as Activity, R.id.nav_settings)
        binding.version = "v" + BuildConfig.VERSION_NAME
        setupDrawer()
    }

    fun setupDrawer() {
        binding.navSettings.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener  when (it.itemId) {
                R.id.settingsPassword -> {
                    viewModel. navController.navigate(R.id.changePasswordFragment)
                    true
                }
                R.id.settingsProfilePicture -> {
                    viewModel.navController.navigate(R.id.profilePictureFragment)
                    true
                }
                R.id.settingsLogOut -> {
                    context?.let { ctx ->
                        CustomInfoDialog(ctx, context?.getString(R.string.are_you_sure_want_to_sign_out), withoutCancel = false, textButtonOk = context?.getString(R.string.yes), onDismiss = {
                            mActivity.showLoading()
                            viewModel.signOut({
                                mActivity.hideLoading()
                                ActivityController.navigateWithClearTop(mActivity, LoginActivity::class.java)
                            }, {
                                mActivity.hideLoading()
                                CustomInfoDialog(ctx, it)
                            })

                        })
                    }
                    true
                }
                else -> { true }
            }
        }
    }
}