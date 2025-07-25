package com.beauty.beautyapp

import android.app.Application
import com.beauty.beautyapp.di.initKoin

class BeautyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
