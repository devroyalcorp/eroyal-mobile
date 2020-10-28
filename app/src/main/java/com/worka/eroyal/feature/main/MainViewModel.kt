package com.worka.eroyal.feature.main

import android.app.Application
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.component.customcomponent.SingleLiveEvent


class MainViewModel(var app: Application) : BaseViewModel(app) {

    var onNavigateToMePage = SingleLiveEvent<Unit>()
}
