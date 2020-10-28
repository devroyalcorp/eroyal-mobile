package com.worka.eroyal.feature.mycustomers

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController
import org.koin.core.KoinComponent

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-10.
 */
class MyCustomersActivity: BaseActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        FragmentController.navigateTo(this, MyCustomerFragment::class.java.name, Bundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishToLeft(this)
    }
}