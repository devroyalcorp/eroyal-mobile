package com.worka.eroyal.control

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.worka.eroyal.R

object FragmentController {
    fun navigateTo(
        context: FragmentActivity?,
        fragmentName: String,
        args: Bundle? = null,
        addToBackStack: Boolean = false,
        reorderingAllowed: Boolean = false
    ) {
        updateFragmentTransaction(context, fragmentName, args, addToBackStack, reorderingAllowed)
    }

    private fun updateFragmentTransaction(
        context: FragmentActivity?,
        fragmentName: String,
        args: Bundle?,
        addToBackStack: Boolean,
        reorderingAllowed: Boolean
    ) {
        val ft = context?.supportFragmentManager?.beginTransaction()
        val fragment = context?.let { Fragment.instantiate(it, fragmentName, args) }
        fragment?.let { ft?.replace(R.id.container, it, fragmentName) }
        if (addToBackStack) ft?.addToBackStack(fragmentName)
        if (reorderingAllowed) ft?.setReorderingAllowed(true)
        ft?.commitAllowingStateLoss()
    }

    fun popBack(activity: FragmentActivity?, targetFragmentName: String = "") {
        val fm = activity?.supportFragmentManager
        if (!targetFragmentName.isEmpty()) {
            fm?.popBackStack(targetFragmentName, 0)
        } else {
            fm?.popBackStackImmediate()
        }
    }

}