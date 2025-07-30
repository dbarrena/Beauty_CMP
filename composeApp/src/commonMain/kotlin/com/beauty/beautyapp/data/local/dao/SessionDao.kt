package com.beauty.beautyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.beauty.beautyapp.model.room.Session

@Dao
interface SessionDao {
    @Upsert
    suspend fun insert(session: Session): Long

    @Query("DELETE FROM session")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM session LIMIT 1")
    suspend fun getSession(): Session?
}