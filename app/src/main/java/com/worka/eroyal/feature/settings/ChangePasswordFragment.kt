package com.worka.eroyal.feature.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.base.Constants.RESET_PASSWORD_TOKEN
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentChangePasswordBinding
import com.worka.eroyal.feature.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-22.
 */
class ChangePasswordFragment : BaseFragment(), TextWatcher {
    private val viewModel: SettingsViewModel by sharedViewModel()
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        mActivity.showLoading()
        with(viewModel) {
            arguments?.getString(RESET_PASSWORD_TOKEN, "")?.run {
                if (!isNullOrBlank()) {
                    resetPasswordToken.set(this@run)
                    currentPasswordVisibility.set(View.GONE)
                    validateResetPasswordToken()
                } else {
                    showLayoutChangePassword()
                }
            } ?: run {
                showLayoutChangePassword()
            }
        }

        binding.etNewPassword.addTextChangedListener(this)
        binding.etConfirmPassword.addTextChangedListener(this)

        binding.btnUpdatePassword.setOnClickListener {
            mActivity.showLoading()
            viewModel.changePassword({
                mActivity.hideLoading()
                context?.let { ctx ->
                    CustomInfoDialog(ctx, ctx.getString(R.string.password_successfully_changed), cancelable = false, withoutCancel = true, onDismiss = {
                        if (!viewModel.isResetPassword()) {
                            onBackPressed()
                        } else {
                            navigateToLoginPage()
                        }
                    })
                }

            },{
                mActivity.hideLoading()
                context?.let { ctx -> CustomInfoDialog(ctx, it, withoutCancel = true, onDismiss = {
                    if (viewModel.isResetPassword()) {
                        navigateToLoginPage()
                    }
                }) }
            })
        }
    }

    private fun showLayoutChangePassword() {
        with(viewModel) {
            currentPasswordVisibility.set(View.VISIBLE)
            changePasswordLayoutVisibility.set(View.VISIBLE)
            mActivity.hideLoading()
        }
    }

    private fun validateResetPasswordToken() {
        viewModel.validateResetTokenPassword({
            viewModel.changePasswordLayoutVisibility.set(View.VISIBLE)
            mActivity.hideLoading()
        }){
            mActivity.hideLoading()
            context?.let { ctx->
                CustomInfoDialog(ctx, it, withoutCancel = true, onDismiss = {
                    navigateToLoginPage()
                })
            }
        }
    }

    private fun navigateToLoginPage() {
        ActivityController.navigateWithClearTop(mActivity, LoginActivity::class.java)
    }

    override fun afterTextChanged(s: Editable?) {
        viewModel.newPassword.get()?.let {
            viewModel.validatePassword()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    fun onBackPressed() {
        if (!viewModel.navController.popBackStack()) {
            finishToRight()
        }
    }
}
