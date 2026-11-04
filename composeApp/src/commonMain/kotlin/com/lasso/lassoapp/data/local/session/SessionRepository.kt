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

        sessionDao.replaceSession(sessionEntity)
    }

    suspend fun getBranches(): List<Session> = sessionDao.getSessions().drop(1)

    suspend fun addBranch(employee: Employee, token: String) {
        require(sessionDao.getSession()?.isAdmin == true) { "Solo los administradores pueden agregar sucursales." }
        require(token.isNotBlank()) { "No se recibió un token de sesión válido." }
        sessionDao.rememberBranch(Session(
            partnerId = employee.partnerId, employeeId = employee.id,
            partnerName = employee.partners?.name.orEmpty(), employeeName = employee.name,
            employeeRole = employee.role, token = token,
        ))
    }

    suspend fun switchBranch(partnerId: Int) = sessionDao.switchBranch(partnerId)

    suspend fun getSession(): Session? {
        val session = sessionDao.getSession()
        return session
    }

    suspend fun updateProfile(employee: Employee) {
        val session = sessionDao.getSession() ?: return
        if (session.employeeId != employee.id) return
        sessionDao.insert(session.copy(employeeName = employee.name, employeeRole = employee.role))
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
