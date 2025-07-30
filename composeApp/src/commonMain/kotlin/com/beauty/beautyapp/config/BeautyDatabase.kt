package com.beauty.beautyapp.config

import androidx.room.Database
import androidx.room.RoomDatabase
import com.beauty.beautyapp.data.local.dao.SessionDao
import com.beauty.beautyapp.model.room.Session

@Database(entities = [Session::class], version = 1)
abstract class BeautyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}