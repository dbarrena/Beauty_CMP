package com.lasso.lassoapp

import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.*
import kotlin.test.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutSubmissionTest {
    private class FakeRepository : CheckoutRepository {
        var calls = 0
        var optionsLoads = 0
        var fail = false
        var snapshot: Sale? = null
        val gate = CompletableDeferred<Unit>()

        override suspend fun employees(): List<Employee> {
            optionsLoads++
            return emptyList()
        }

        override suspend fun employeeId(): Int? = null

        override suspend fun submit(sale: Sale) {
            calls++
            snapshot = sale
            if (fail) error("Test failure")
            gate.await()
        }
    }

    @Test
    fun reopeningDuringSubmissionResumesAndCompletionIsConsumedOnce() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val model = CheckoutDialogViewModelV2(repository)
            model.beginCheckout()
            runCurrent()
            model.navigateToSplitPayment(CheckoutPaymentMethod.Cash)
            val item =
                SelectedPosItem(
                    lassoItem = Product(name = "Labial", price = "250", commissionPercentage = null)
                )
            val payments = listOf(CheckoutPayment(CheckoutPaymentMethod.Cash, 250.0))
            model.registerSale(listOf(item), payments)
            item.quantity = 9
            model.beginCheckout()
            model.registerSale(listOf(item), payments)
            runCurrent()
            assertTrue(model.state.value.isSubmitting)
            assertEquals(1, repository.calls)
            assertEquals(1, repository.optionsLoads)
            assertEquals(1, repository.snapshot!!.saleDetails.single().quantity)
            assertEquals(payments, model.state.value.draftPayments)
            repository.gate.complete(Unit)
            runCurrent()
            val completed = model.state.value.completion!!
            assertEquals(1, completed.items.single().quantity)
            assertIs<CheckoutStep.Success>(model.state.value.step)
            model.acknowledgeCompletion(completed.id, dialogVisible = false)
            assertNull(model.state.value.completion)
            model.acknowledgeCompletion(completed.id, dialogVisible = false)
            model.beginCheckout()
            runCurrent()
            assertIs<CheckoutStep.MethodPicker>(model.state.value.step)
            assertEquals(2, repository.optionsLoads)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun failedSubmissionRetainsDraftAndCanBeRetried() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository().apply { fail = true }
            val model = CheckoutDialogViewModelV2(repository)
            model.beginCheckout()
            runCurrent()
            model.navigateToSplitPayment(CheckoutPaymentMethod.Cash)
            val items =
                listOf(
                    SelectedPosItem(
                        lassoItem =
                            Product(name = "Labial", price = "250", commissionPercentage = null)
                    )
                )
            val payments = listOf(CheckoutPayment(CheckoutPaymentMethod.Cash, 250.0))
            model.registerSale(items, payments)
            runCurrent()
            model.beginCheckout()
            runCurrent()
            assertEquals("Test failure", model.state.value.error)
            assertEquals(payments, model.state.value.draftPayments)
            assertFalse(model.state.value.isSubmitting)
            repository.fail = false
            model.registerSale(items, payments)
            runCurrent()
            assertEquals(2, repository.calls)
            repository.gate.complete(Unit)
            runCurrent()
            assertIs<CheckoutStep.Success>(model.state.value.step)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
