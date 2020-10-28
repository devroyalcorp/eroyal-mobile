package com.worka.eroyal.feature.orders

import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.component.CustomInfoDialog
import com.worka.eroyal.control.FragmentController
import com.worka.eroyal.databinding.ActivityFragmentContainerBinding

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 26/06/20.
 */
class OrdersActivity: BaseActivity() {

    private lateinit var binding: ActivityFragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fragment_container)

        FragmentController.navigateTo(this, OrdersFragment::class.java.name)
    }

    override fun onBackPressed() {

    }
}
