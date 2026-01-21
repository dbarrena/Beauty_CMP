package com.lasso.lassoapp.di

import com.lasso.lassoapp.AppViewModel
import com.lasso.lassoapp.config.LassoDatabase
import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.data.remote.KtorLassoApi
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.cash_closure.create.CashClosureViewModel
import com.lasso.lassoapp.screens.cash_closure.records.CashClosureRecordsScreenViewModel
import com.lasso.lassoapp.screens.configuration.ConfigurationViewModel
import com.lasso.lassoapp.screens.home.HomeScreenViewModel
import com.lasso.lassoapp.screens.login.LoginScreenViewModel
import com.lasso.lassoapp.screens.pos.PosViewModel
import com.lasso.lassoapp.screens.pos.checkout.CheckoutDialogViewModel
import com.lasso.lassoapp.screens.product_service.ProductServiceViewModel
import com.lasso.lassoapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.lasso.lassoapp.screens.sales.SalesScreenViewModel
import com.lasso.lassoapp.screens.sales.detail.SaleDetailsDialogScreenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Application.Json)
            }
        }
    }

    single<SessionRepository> { SessionRepository(get()) }
    single<LassoApi> { KtorLassoApi(get(), get()) }
}

val viewModelModule = module {
    factoryOf(::PosViewModel)
    factoryOf(::ProductServiceDialogViewModel)
    factoryOf(::CheckoutDialogViewModel)
    factoryOf(::SalesScreenViewModel)
    factoryOf(::ConfigurationViewModel)
    factoryOf(::ProductServiceViewModel)
    factoryOf(::HomeScreenViewModel)
    factoryOf(::AppViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::CalendarScreenViewModel)
    factoryOf(::CashClosureViewModel)
    factoryOf(::SaleDetailsDialogScreenViewModel)
    factoryOf(::CashClosureRecordsScreenViewModel)
}

fun daoModule() = module {
    single<SessionDao> { get<LassoDatabase>().sessionDao() }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            platformModule(),
            daoModule(),
            dataModule,
            viewModelModule,
        )
    }
}

fun initKoinIos() = initKoin(appDeclaration = {})
