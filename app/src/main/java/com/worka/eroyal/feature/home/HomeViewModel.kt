package com.worka.eroyal.feature.home

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.base.Constants.BRANCH
import com.worka.eroyal.base.Constants.MENU_ITEM
import com.worka.eroyal.base.Constants.ROLE
import com.worka.eroyal.component.customcomponent.SingleLiveEvent
import com.worka.eroyal.data.home.Activity
import com.worka.eroyal.data.home.MenuHome
import com.worka.eroyal.data.report.Role
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.getGreetingMessage
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.home.helper.HomeMenu
import com.worka.eroyal.repository.HomeRepository
import com.worka.eroyal.utils.DateUtils
import org.koin.core.inject
import java.util.*

class HomeViewModel(val app: Application) : BaseViewModel(app) {

    private val repository: HomeRepository by inject()
    val homeMenus = mutableListOf<MenuHome>()

    var greatingWithUsername = ObservableField("")
    var taskRemaining = MutableLiveData<Int>()
    var activityList = arrayListOf<ActivityViewModel>()

    var onUpdateActivity = SingleLiveEvent<Unit>()

    var emptyActivitiesVisibility = ObservableField(View.GONE)
    var activityListVisibility = ObservableField(View.VISIBLE)

    fun setGreating() {
       greatingWithUsername.set(getAppContext().resources.getString(R.string.hello).plus(" ").plus(user.name).plus(",\n")
            .plus(getGreetingMessage(Calendar.getInstance())))
    }

    fun getHomeData(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getHomeData({
            taskRemaining.value = it?.data?.customerList?.size
            it?.data?.menus?.forEach { menuHome ->
                homeMenus.add(MenuHome(menuHome, true))
            }
            sessionStorage.put(BRANCH, it?.data?.branch)
            sessionStorage.put(MENU_ITEM, it?.data?.menus)
            sessionStorage.put(ROLE, it?.data?.role)
            getActivities(cbOnSuccess, cbOnError)
            cbOnSuccess.invoke()
        }, cbOnError)
    }

    fun getHomeMenu(onClick: (String) -> Unit): List<SimpleViewModel> {
        val menus = mutableListOf<HomeMenuViewModel>()
        enumValues<HomeMenu>().forEach {
            menus.add(
                HomeMenuViewModel(
                    it.iconResActive,
                    it.iconResInActive,
                    getAppContext().getString(it.titleRes),
                    isMenuEnabled(it.type)) { onClick.invoke(it) })
        }
        return menus
    }

    private fun isMenuEnabled(type: String) : Boolean {
        return if (type.equals("eroyal_orders_index")) {
            homeMenus.find { it.type.equals(type) } != null && isSalesOrSPGRole()
        } else {
            homeMenus.find { it.type.equals(type) } != null
        }
    }

    private fun isSalesOrSPGRole(): Boolean {
        val role = sessionStorage.get(ROLE, Role::class.java).hierarchy.orEmpty()
        return role.equals(Constants.SALES, true) || role.equals(Constants.SPG, true)
    }

    fun getActivities(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.getActivities({
            setupActivityList(it?.activities)
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })
    }

    private fun setupActivityList(list: ArrayList<Activity>?){
        activityList.clear()
        list?.forEachIndexed {index, it ->
                if (index == list.size - 1) {
                    activityList.add(ActivityViewModel(
                        it.activityType,
                        it.description,
                        getActivityDateTime(it.createdDate),
                        View.GONE))
                } else {
                    activityList.add(ActivityViewModel(it.activityType, it.description, getActivityDateTime(it.createdDate)))
                }
        }
        when {
            activityList.isNullOrEmpty() -> {
                emptyActivitiesVisibility.set(View.VISIBLE)
                activityListVisibility.set(View.GONE)
            }
            else -> {
                emptyActivitiesVisibility.set(View.GONE)
                activityListVisibility.set(View.VISIBLE)
            }
        }
        onUpdateActivity.invoke()
    }

    private fun getActivityDateTime(dateTime: String?): String {
        return getAppContext().getString(R.string.today_at).plus(" ").plus(DateUtils.formateDate(dateTime, DateUtils.YYYY_MM_DDTHH_MM_SSSZ, DateUtils.HH_MM_AAA))
    }
}
