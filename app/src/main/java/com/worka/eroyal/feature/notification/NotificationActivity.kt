package com.worka.eroyal.feature.notification

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-19.
 */
class NotificationActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        FragmentController.navigateTo(this, NotificationsFragment::class.java.name, Bundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishToLeft(this)
    }
}