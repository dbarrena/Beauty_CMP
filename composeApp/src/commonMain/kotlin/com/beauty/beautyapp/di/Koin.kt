package com.beauty.beautyapp.di

import com.beauty.beautyapp.data.BeautyApi
import com.beauty.beautyapp.data.InMemoryMuseumStorage
import com.beauty.beautyapp.data.KtorBeautyApi
import com.beauty.beautyapp.data.KtorMuseumApi
import com.beauty.beautyapp.data.MuseumApi
import com.beauty.beautyapp.data.MuseumRepository
import com.beauty.beautyapp.data.MuseumStorage
import com.beauty.beautyapp.screens.detail.DetailViewModel
import com.beauty.beautyapp.screens.list.ListViewModel
import com.beauty.beautyapp.screens.pos.PosViewModel
import com.beauty.beautyapp.screens.pos.checkout.CheckoutDialogViewModel
import com.beauty.beautyapp.screens.product.dialog.ProductDialogViewModel
import com.beauty.beautyapp.screens.sales.SalesScreenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Application.Json)
            }
        }
    }

    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single<BeautyApi> { KtorBeautyApi(get()) }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }
}

val viewModelModule = module {
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
    factoryOf(::PosViewModel)
    factoryOf(::ProductDialogViewModel)
    factoryOf(::CheckoutDialogViewModel)
    factoryOf(::SalesScreenViewModel)
}

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
