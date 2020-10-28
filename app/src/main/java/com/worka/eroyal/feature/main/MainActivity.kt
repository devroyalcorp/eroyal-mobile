package com.worka.eroyal.feature.main

import android.os.Bundle
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import com.orhanobut.hawk.Hawk
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.Constants
import com.worka.eroyal.component.NotificationDialog
import com.worka.eroyal.control.ActivityController
import com.worka.eroyal.control.FragmentController
import com.worka.eroyal.data.bus.OpenNotification
import com.worka.eroyal.databinding.ActivityWithLeftDrawerBinding
import com.worka.eroyal.feature.mycustomers.MyCustomersActivity
import com.worka.eroyal.feature.myteam.MyTeamActivity
import com.worka.eroyal.feature.notification.NotificationActivity
import com.worka.eroyal.feature.report.ReportActivity
import com.worka.eroyal.feature.settings.SettingsActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {

    lateinit var binding: ActivityWithLeftDrawerBinding
    private var notificationDialog: NotificationDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_with_left_drawer, null, false)
        setContentView(binding.root)
        FragmentController.navigateTo(this, MainFragment::class.java.name)
        setupDrawer()
    }

    fun setupDrawer() {
        val menus = Hawk.get<String>(Constants.MENU_ITEM).orEmpty()
        binding.navView.menu.findItem(R.id.leftDrawerMyCustomers).isVisible = menus.contains(Constants.MY_CUSTOMER_MENU)
        binding.navView.menu.findItem(R.id.leftDrawerMyReport).isVisible = menus.contains(Constants.MY_REPORTS_MENU)
        binding.navView.menu.findItem(R.id.leftDrawerMyTeam).isVisible = menus.contains(Constants.MY_TEAMS_MENU)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId){
            R.id.leftDrawerNotification -> ActivityController.navigateToRight(this, NotificationActivity::class.java)
            R.id.leftDrawerMyCustomers -> ActivityController.navigateToRight(this, MyCustomersActivity::class.java)
            R.id.leftDrawerMyReport -> ActivityController.navigateToRight(this, ReportActivity::class.java)
            R.id.leftDrawerMyTeam -> ActivityController.navigateToRight(this, MyTeamActivity::class.java)
            R.id.leftDrawerSettings -> ActivityController.navigateToRight(this, SettingsActivity::class.java)
        }
        binding.drawerLayout.closeDrawer(Gravity.LEFT)
        return@setNavigationItemSelectedListener true
    }
    binding.navView.invalidate()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openNotificationPage(openNotification: OpenNotification){
        notificationDialog =  NotificationDialog(openNotification.message, this){
            ActivityController.navigateToRight(this, NotificationActivity::class.java)
        }
        notificationDialog?.let {
            if (it.isShowing) {
                notificationDialog?.dismiss()
            }
        }
        notificationDialog?.show()
    }

}
