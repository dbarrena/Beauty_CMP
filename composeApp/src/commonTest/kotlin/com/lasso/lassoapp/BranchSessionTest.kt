package com.lasso.lassoapp

import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Partner
import com.lasso.lassoapp.model.room.Session
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class BranchSessionTest {
    private class MemoryDao : SessionDao {
        val rows = mutableMapOf<Int, Session>()
        private var nextId = 1
        override suspend fun insert(session: Session): Long {
            val id = if (session.id == 0) nextId++ else session.id
            rows[id] = session.copy(id = id)
            return id.toLong()
        }
        override suspend fun getSession() = getSessions().firstOrNull()
        override suspend fun getSessions() = rows.values.sortedBy { it.id }
        override suspend fun deleteAll(): Int = rows.size.also { rows.clear() }
    }

    private fun employee(partnerId: Int, role: String = "administrador") = Employee(
        id = partnerId * 10, name = "Ana", email = "ana$partnerId@example.com", role = role,
        partnerId = partnerId, updatedAt = null, partners = Partner(partnerId, "Sucursal $partnerId", "business@example.com"),
    )

    @Test
    fun branchesPersistWithoutChangingActiveSessionAndCanSwitchBack() = runTest {
        val dao = MemoryDao()
        val repository = SessionRepository(dao)
        repository.saveSession(employee(1), "token-one")
        val original = repository.getSession()
        repository.addBranch(employee(2), "token-two")
        assertEquals(original, repository.getSession())
        repository.addBranch(employee(2), "refreshed-token-two")
        assertEquals(1, repository.getBranches().size)
        val reopened = SessionRepository(dao)
        assertEquals("Sucursal 2", reopened.getBranches().single().partnerName)
        reopened.switchBranch(2)
        assertEquals(2, reopened.getPartnerId())
        assertEquals(20, reopened.getEmployeeId())
        assertEquals("refreshed-token-two", reopened.getToken())
        assertEquals(1, reopened.getBranches().single().partnerId)
        reopened.switchBranch(1)
        assertEquals("token-one", reopened.getToken())
        assertEquals(original, reopened.getSession())
        reopened.removeSession()
        assertTrue(dao.rows.isEmpty())
    }

    @Test
    fun invalidAndUnauthorizedBranchesDoNotModifyStoredSessions() = runTest {
        val dao = MemoryDao()
        val repository = SessionRepository(dao)
        repository.saveSession(employee(1), "token-one")
        assertFailsWith<IllegalArgumentException> { repository.addBranch(employee(1), "duplicate") }
        assertFailsWith<IllegalArgumentException> { repository.addBranch(employee(2), "") }
        assertFailsWith<IllegalStateException> { repository.switchBranch(999) }
        assertEquals("token-one", repository.getToken())
        assertTrue(repository.getBranches().isEmpty())
        repository.saveSession(employee(1, "empleado"), "employee-token")
        assertFailsWith<IllegalArgumentException> { repository.addBranch(employee(2), "token-two") }
        assertTrue(repository.getBranches().isEmpty())
    }
}
