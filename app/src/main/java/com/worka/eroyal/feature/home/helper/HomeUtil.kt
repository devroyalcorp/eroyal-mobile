package com.worka.eroyal.feature.home.helper

import com.worka.eroyal.R

enum class HomeMenu(var type: String, var titleRes: Int, var iconResActive: Int, var iconResInActive: Int) {
    TASKS("eroyal_tasks_index", R.string.tasks, R.drawable.ic_task_green, R.drawable.ic_tasks),
    VISITS("eroyal_visits_index", R.string.visits, R.drawable.ic_location, R.drawable.ic_location_grey),
    ORDERS("eroyal_orders_index", R.string.orders, R.drawable.ic_order_active, R.drawable.ic_order),
    ME("eroyal_my_visits_index", R.string.me, R.drawable.ic_me_active, R.drawable.ic_me)
}
