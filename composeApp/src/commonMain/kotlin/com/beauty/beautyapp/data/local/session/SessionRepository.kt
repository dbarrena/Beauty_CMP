package com.beauty.beautyapp.data.local.session

import com.beauty.beautyapp.data.local.dao.SessionDao
import com.beauty.beautyapp.model.Employee
import com.beauty.beautyapp.model.room.Session

class SessionRepository(
    private val sessionDao: SessionDao
) {
    suspend fun saveSession(employee: Employee) {
        val sessionEntity = Session(
            employeeId = employee.id,
            partnerId = employee.partnerId,
            partnerName = employee.partners.name,
            employeeName = employee.name,
            employeeRole = employee.role
        )

        sessionDao.insert(sessionEntity)
    }

    suspend fun getSession(): Session? {
        val session = sessionDao.getSession()
        println("session: $session")
        return session
    }

    suspend fun removeSession() {
        sessionDao.deleteAll()
    }

    suspend fun getPartnerId(): Int? {
        return sessionDao.getSession()?.partnerId
    }

    suspend fun isLoggedIn(): Boolean {
        sessionDao.getSession()?.let {
            return true
        } ?: run {
            return false
        }
    }
}