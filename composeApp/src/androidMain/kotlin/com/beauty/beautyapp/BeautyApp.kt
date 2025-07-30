package com.beauty.beautyapp

import android.app.Application
import com.beauty.beautyapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class BeautyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            appDeclaration = { androidContext(this@BeautyApp) }
        )
    }
}
