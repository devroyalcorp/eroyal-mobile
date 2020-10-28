package com.worka.eroyal.feature.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.databinding.FragmentForgotPasswordBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-16.
 */
class ForgotPasswordFragment : BaseFragment(){

    private val viewModel: ForgotPasswordViewModel by sharedViewModel()

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel

        binding.btnForgotPassword.setOnClickListener {
            mActivity.showLoading()
            viewModel.resetPassword({
                mActivity.hideLoading()
                context?.let { ctx ->
                    CustomInfoDialog(ctx, context?.getString(R.string.success_reset_password), onDismiss = {
                        ActivityController.finishToLeft(mActivity)
                    }, withoutCancel = true)
                }
            },{
                mActivity.hideLoading()
                context?.let { ctx -> CustomInfoDialog(ctx, it) }
            })
        }
    }

}