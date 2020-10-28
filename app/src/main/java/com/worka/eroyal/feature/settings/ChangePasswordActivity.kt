package com.worka.eroyal.feature.settings

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.Constants.FORGOT_PASSWORD
import com.worka.eroyal.base.Constants.RESET_PASSWORD_TOKEN
import com.worka.eroyal.base.Constants.TOKEN
import com.worka.eroyal.control.FragmentController

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 13/08/20.
 */
class ChangePasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        intent.data?.also { uri ->
            when (uri.host) {
                FORGOT_PASSWORD -> {
                    FragmentController.navigateTo(this, ChangePasswordFragment::class.java.name, Bundle().apply {
                        putString(RESET_PASSWORD_TOKEN, uri.getQueryParameter(TOKEN))
                    })
                }
            }
        }
    }
}
