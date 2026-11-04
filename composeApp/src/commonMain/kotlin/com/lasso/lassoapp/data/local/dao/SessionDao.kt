package com.lasso.lassoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Transaction
import com.lasso.lassoapp.model.room.Session

@Dao
interface SessionDao {
    // The oldest row is the active session. Other rows retain authenticated branches.
    // Switching swaps their contents atomically, preserving the active row's ID.
    @Upsert
    suspend fun insert(session: Session): Long

    @Query("DELETE FROM session")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM session ORDER BY id LIMIT 1")
    suspend fun getSession(): Session?

    @Query("SELECT * FROM session ORDER BY id")
    suspend fun getSessions(): List<Session>

    @Transaction
    suspend fun rememberBranch(session: Session) {
        val sessions = getSessions()
        val active = sessions.firstOrNull() ?: error("No hay una sesión activa.")
        require(active.partnerId != session.partnerId) { "Esta sucursal ya está activa." }
        val existing = sessions.firstOrNull { it.partnerId == session.partnerId }
        insert(session.copy(id = existing?.id ?: 0))
    }

    @Transaction
    suspend fun switchBranch(partnerId: Int) {
        val sessions = getSessions()
        val active = sessions.firstOrNull() ?: error("No hay una sesión activa.")
        val target = sessions.firstOrNull { it.partnerId == partnerId }
            ?: error("No se encontró la sucursal.")
        require(target.token.isNotBlank()) { "Inicia sesión de nuevo en esta sucursal." }
        if (target.id == active.id) return
        insert(target.copy(id = active.id))
        insert(active.copy(id = target.id))
    }

    @Transaction
    suspend fun replaceSession(session: Session) {
        deleteAll()
        insert(session)
    }
}
