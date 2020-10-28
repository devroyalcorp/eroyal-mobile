package com.worka.eroyal.feature.tasks

import android.view.View
import com.worka.eroyal.R
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.tasks.TaskItemViewModel.TaskState.COMPLETE
import com.worka.eroyal.feature.tasks.TaskItemViewModel.TaskState.INCOMPLETE

class TaskItemViewModel(
    var id: Int?,
    var stateTask: String? = COMPLETE.state,
    var customerName: String?,
    var taskCreator: String?,
    var iconLocationVisibility: Int = View.VISIBLE,
    var checkoutTime: String?,
    var isVisitedItem: Boolean = false,
    var description: String? = "",
    var visitedStatus: String? = COMPLETE.state,
    var onClick:(Int?) -> Unit
) : SimpleViewModel {
    override fun layoutId(): Int = R.layout.item_task

    fun onClick() {
        if (stateTask.equals(INCOMPLETE.state)) {
            onClick.invoke(id)
        }
    }

    fun getEnabled(): Boolean{
        return when (stateTask) {
            COMPLETE.state -> false
            INCOMPLETE.state -> true
            else -> true
        }
    }

    val timeVisibility: Int
    get() {
       return if(getEnabled() || isVisitedItem) View.GONE else View.VISIBLE
    }

    fun getCheckVisible(): Int {
        return if(getEnabled()){
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    fun getIconLocation(): Int {
        return when (stateTask) {
            COMPLETE.state -> R.drawable.ic_location_grey
            INCOMPLETE.state -> R.drawable.ic_location_black
            else -> R.drawable.ic_location_black
        }
    }

    fun getVisitedStatusIcon(): Int {
        return when (visitedStatus){
            COMPLETE.state -> R.drawable.ic_checked
            else -> R.drawable.ic_cross_red
        }
    }

    fun getTextColor(): Int {
        return when (stateTask) {
            COMPLETE.state -> if(!isVisitedItem) R.color.colorGrey else R.color.colorBlack
            INCOMPLETE.state -> R.color.colorBlack
            else -> R.color.colorBlack
        }

    }

    enum class TaskState(var state: String) {
        COMPLETE("complete"),
        INCOMPLETE("approve")
    }
}

