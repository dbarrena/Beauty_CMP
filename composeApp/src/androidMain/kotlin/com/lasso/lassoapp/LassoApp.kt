package com.lasso.lassoapp

import android.app.Application
import com.lasso.lassoapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class LassoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            appDeclaration = { androidContext(this@LassoApp) }
        )
    }
}
