package com.worka.eroyal.base

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View



open class BaseFragment : Fragment() {
    lateinit var mActivity: BaseActivity

    val bundle = Bundle()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity as BaseActivity
    }

    fun goTBack() {
        FragmentController.popBack(activity)
    }

    fun finishToRight(){
        ActivityController.finishToLeft(activity)
    }

}