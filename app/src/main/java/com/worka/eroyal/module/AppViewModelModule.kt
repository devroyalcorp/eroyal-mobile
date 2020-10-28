package com.worka.eroyal.module

import com.worka.eroyal.feature.clockinout.ClockInOutViewModel
import com.worka.eroyal.feature.forgotpassword.ForgotPasswordViewModel
import com.worka.eroyal.feature.home.HomeViewModel
import com.worka.eroyal.feature.login.LoginViewModel
import com.worka.eroyal.feature.main.MainViewModel
import com.worka.eroyal.feature.me.MeViewModel
import com.worka.eroyal.feature.mycustomers.MyCustomerViewModel
import com.worka.eroyal.feature.myteam.MyTeamViewModel
import com.worka.eroyal.feature.notification.NotificationViewModel
import com.worka.eroyal.feature.orders.OrdersViewModel
import com.worka.eroyal.feature.report.ReportViewModel
import com.worka.eroyal.feature.settings.SettingsViewModel
import com.worka.eroyal.feature.tasks.TasksViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appViewModelModule = module {
    viewModel { LoginViewModel(androidApplication()) }
    viewModel { HomeViewModel(androidApplication()) }
    viewModel { TasksViewModel(androidApplication()) }
    viewModel { MainViewModel(androidApplication()) }
    viewModel { ForgotPasswordViewModel(androidApplication()) }
    viewModel { MeViewModel(androidApplication()) }
    viewModel { SettingsViewModel(androidApplication()) }
    viewModel { NotificationViewModel(androidApplication()) }
    viewModel { ClockInOutViewModel(androidApplication()) }
    viewModel { MyCustomerViewModel(androidApplication()) }
    viewModel { ReportViewModel(androidApplication()) }
    viewModel { MyTeamViewModel(androidApplication()) }
    viewModel { OrdersViewModel(androidApplication()) }
}
