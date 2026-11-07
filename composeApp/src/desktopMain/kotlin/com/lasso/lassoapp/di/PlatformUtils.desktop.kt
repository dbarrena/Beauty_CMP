package com.lasso.lassoapp.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.lasso.lassoapp.config.LassoDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual fun platformModule(): Module = module {
    single<LassoDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val directory = when {
            os.contains("win") -> File(System.getenv("LOCALAPPDATA") ?: System.getProperty("user.home"), "Lasso")
            os.contains("mac") -> File(System.getProperty("user.home"), "Library/Application Support/Lasso")
            else -> File(System.getenv("XDG_DATA_HOME") ?: "${System.getProperty("user.home")}/.local/share", "lasso")
        }
        check(directory.isDirectory || directory.mkdirs()) { "No se pudo crear la carpeta de datos de Lasso." }
        Room.databaseBuilder<LassoDatabase>(name = File(directory, "lasso.db").absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
