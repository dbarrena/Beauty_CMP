package com.beauty.beautyapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.beauty.beautyapp.config.BeautyDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<BeautyDatabase> {
        getBeautyDatabase(get()).build()
    }
}


fun getBeautyDatabase(context: Context): RoomDatabase.Builder<BeautyDatabase> {
    val dbFile = context.getDatabasePath("beauty.db")
    return Room.databaseBuilder<BeautyDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver()) // Very important
        .setQueryCoroutineContext(Dispatchers.IO)
}