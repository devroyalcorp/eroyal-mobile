package com.worka.eroyal.feature.settings

import android.os.Bundle
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-11-21.
 */
class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        FragmentController.navigateTo(this, SettingsFragment::class.java.name, Bundle())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishToLeft(this)
    }
}