package com.worka.eroyal.feature.myteam

import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel

/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2020-01-21.
 */
class MyTeamItemViewModel(var id: Int, var imgAvatar: String?, var name: String?, var customerName: String?, var dateTime: String?,
                          var taskCount: String, var visitCount: String,var otherVisits: String, var failedTask: String, var cbOnSelected:(Int) -> Unit): SimpleViewModel {


    override fun layoutId(): Int = R.layout.item_my_team

    fun onSelect(){
        cbOnSelected.invoke(id)
    }


}
