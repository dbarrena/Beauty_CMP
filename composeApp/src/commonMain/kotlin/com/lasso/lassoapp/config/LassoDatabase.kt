package com.lasso.lassoapp.config

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.model.room.Session

@Database(entities = [Session::class], version = 1)
@ConstructedBy(LassoDatabaseConstructor::class)
abstract class LassoDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LassoDatabaseConstructor : RoomDatabaseConstructor<LassoDatabase> {
    override fun initialize(): LassoDatabase
}
