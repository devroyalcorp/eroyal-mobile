package com.worka.eroyal.feature.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentLoginBinding
import com.worka.eroyal.feature.forgotpassword.ForgotPasswordActivity
import com.worka.eroyal.feature.main.MainActivity
import com.worka.eroyal.utils.TextWatcherHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class LoginFragment : BaseFragment() {
    private val viewModel: LoginViewModel by sharedViewModel()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            com.worka.eroyal.R.layout.fragment_login,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        setupLoadingBar()

        binding.btnLogin.setOnClickListener {
            mActivity.showLoading()
            viewModel.login({
                getFirebaseToken()
            }, {
                context?.let { ctx -> CustomInfoDialog(ctx, it, withoutCancel = true) }
                viewModel.password.set("")
                mActivity.hideLoading()
            })
        }
        binding.btnForgotPassword.setOnClickListener {
            ActivityController.navigateToRight(mActivity, ForgotPasswordActivity::class.java)
        }

        binding.etUsername.addTextChangedListener(TextWatcherHelper {
            viewModel.validate()
        })
        binding.etPassword.addTextChangedListener(TextWatcherHelper {
            viewModel.validate()
        })
    }

    fun setupLoadingBar() {
        Handler().postDelayed({ checkingSession() }, 1000)
    }

    fun checkingSession() {
        if (!viewModel.isHaveActiveSession()) {
            showLoginForm()
        } else {
            viewModel.checkAccessTokenExpired({
                getFirebaseToken()
            }, {
                showLoginForm()
            })

        }
    }

    fun showLoginForm() {
        binding.loadingBar.visibility = View.GONE
        setVisibilityLoginForm(View.VISIBLE)
    }

    fun setVisibilityLoginForm(visibility: Int) {
        binding.layoutLoginForm.visibility = visibility
        binding.btnForgotPassword.visibility = visibility
        binding.btnLogin.visibility = visibility
    }

    fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Get Firebase Token", "getInstanceId failed", task.exception)
                    navigateToHome()
                    return@OnCompleteListener
                }

                val token = task.result?.token
                viewModel.registerFCMToken(token.orEmpty()) {
                    navigateToHome()
                }
            })
    }

    fun navigateToHome() {
        mActivity.hideLoading()
        ActivityController.navigateWithClearTop(mActivity, MainActivity::class.java)
        mActivity.finishAffinity()
    }

}
