package com.lasso.lassoapp.data.local.session

import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.room.Session

class SessionRepository(
    private val sessionDao: SessionDao
) {
    suspend fun saveSession(employee: Employee, token: String) {
        val sessionEntity = Session(
            employeeId = employee.id,
            partnerId = employee.partnerId,
            partnerName = employee.partners?.name.orEmpty(),
            employeeName = employee.name,
            employeeRole = employee.role,
            token = token,
        )

        sessionDao.deleteAll()
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

    suspend fun getEmployeeId(): Int? {
        return sessionDao.getSession()?.employeeId
    }

    suspend fun getToken(): String? = sessionDao.getSession()?.token

    suspend fun isLoggedIn(): Boolean {
        return !sessionDao.getSession()?.token.isNullOrBlank()
    }
}
