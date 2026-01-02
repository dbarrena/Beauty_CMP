package com.lasso.lassoapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.lasso.lassoapp.config.LassoDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<LassoDatabase> {
        getLassoDatabase(get()).build()
    }
}


fun getLassoDatabase(context: Context): RoomDatabase.Builder<LassoDatabase> {
    val dbFile = context.getDatabasePath("lasso.db")
    return Room.databaseBuilder<LassoDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver()) // Very important
        .setQueryCoroutineContext(Dispatchers.IO)
}