package com.worka.eroyal.feature.forgotpassword

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.control.FragmentController

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-16.
 */
class ForgotPasswordActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        FragmentController.navigateTo(this, ForgotPasswordFragment::class.java.name, Bundle())
    }
}