package com.beauty.beautyapp.di

import com.beauty.beautyapp.AppViewModel
import com.beauty.beautyapp.config.BeautyDatabase
import com.beauty.beautyapp.data.local.dao.SessionDao
import com.beauty.beautyapp.data.local.session.SessionRepository
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.data.remote.KtorBeautyApi
import com.beauty.beautyapp.screens.configuration.ConfigurationViewModel
import com.beauty.beautyapp.screens.home.HomeScreenViewModel
import com.beauty.beautyapp.screens.login.LoginScreenViewModel
import com.beauty.beautyapp.screens.pos.PosViewModel
import com.beauty.beautyapp.screens.pos.checkout.CheckoutDialogViewModel
import com.beauty.beautyapp.screens.product_service.ProductServiceViewModel
import com.beauty.beautyapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.beauty.beautyapp.screens.sales.SalesScreenViewModel
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
    single<BeautyApi> { KtorBeautyApi(get(), get()) }
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
}

fun daoModule() = module {
    single<SessionDao> { get<BeautyDatabase>().sessionDao() }
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
