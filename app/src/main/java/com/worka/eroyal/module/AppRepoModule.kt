package com.worka.eroyal.module

import com.worka.eroyal.base.Constants.LONG_TIME_OUT
import com.worka.eroyal.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appRepoModule = module {
    factory { LoginRepository(get()) }
    factory { HomeRepository(get()) }
    factory { TasksRepository(get(named(LONG_TIME_OUT))) }
    factory { ForgotPasswordRepository(get()) }
    factory { SettingsRepository(get()) }
    factory { MeRepository(get()) }
    factory { NotificationRepository(get()) }
    factory { ClockInOutRepository(get(named(LONG_TIME_OUT))) }
    factory { MyCustomerRepository(get()) }
    factory { ReportRepository(get()) }
    factory { MyTeamRepository(get()) }
    factory { OrdersRepository(get()) }
}
