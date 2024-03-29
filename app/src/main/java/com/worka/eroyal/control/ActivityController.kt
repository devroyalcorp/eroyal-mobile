package com.worka.eroyal.control

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.worka.eroyal.R
import org.koin.core.KoinComponent

object ActivityController : KoinComponent {
    fun navigateTo(
        from: Activity?,
        to: Class<out Any>,
        finish: Boolean = false,
        bundle: Bundle? = null
    ) {
        navigateToRight(from, to, finish, bundle, false)
    }

    fun navigateToRight(
        from: Activity?,
        to: Class<out Any>,
        finish: Boolean = false,
        bundle: Bundle? = null,
        isWithAnimation: Boolean = true
    ) {
        val intent = Intent(from, to)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        from?.startActivity(intent)
        if (isWithAnimation) from?.overridePendingTransition(R.anim.anim_enter_left, R.anim.anim_sticky)
        if (finish) from?.finish()
    }

    fun finishToLeft(from: Activity?){
        from?.finish()
        from?.overridePendingTransition(R.anim.anim_sticky, R.anim.anim_leave_right)
    }


    fun navigateWithClearTop(from: Activity, to: Class<out Any>, data: Bundle? = null) {
        val intent = Intent(from, to)
        if (data != null)
            intent.putExtras(data)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        from.startActivity(intent)
        from.finishAffinity()
    }
}