package com.worka.eroyal.feature.login

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.control.FragmentController


class LoginActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        FragmentController.navigateTo(this, LoginFragment::class.java.name, Bundle())
    }
}
