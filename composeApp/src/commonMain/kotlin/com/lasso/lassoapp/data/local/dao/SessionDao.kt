package com.lasso.lassoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lasso.lassoapp.model.room.Session

@Dao
interface SessionDao {
    @Upsert
    suspend fun insert(session: Session): Long

    @Query("DELETE FROM session")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM session LIMIT 1")
    suspend fun getSession(): Session?
}