package com.beauty.beautyapp.config

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.beauty.beautyapp.data.local.dao.SessionDao
import com.beauty.beautyapp.model.room.Session

@Database(entities = [Session::class], version = 1)
@ConstructedBy(BeautyDatabaseConstructor::class)
abstract class BeautyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BeautyDatabaseConstructor : RoomDatabaseConstructor<BeautyDatabase> {
    override fun initialize(): BeautyDatabase
}
