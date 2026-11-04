package com.lasso.lassoapp

import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.reconcileSubmittedItems
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.*
import com.lasso.lassoapp.screens.utils.*
import kotlin.test.*

class AccessibilityRegressionTest {
    @Test
    fun placementKeepsAUsableBodyAndMovesActionsLast() {
        assertEquals(ContentPlacement(true, true), contentPlacement(600, 180, 80, 96))
        assertEquals(ContentPlacement(false, true), contentPlacement(300, 180, 80, 96))
        assertEquals(ContentPlacement(false, false), contentPlacement(150, 180, 80, 96))
    }

    @Test
    fun amountsKeepTheirPreferredSizeOrUseTheLargestFittingStep() {
        assertEquals(48f, fittingFontSize(48f) { true })
        assertEquals(31f, fittingFontSize(48f) { it <= 31.5f })
        assertEquals(16f, fittingFontSize(48f) { false })
        assertEquals(14f, fittingFontSize(14f) { false })
    }

    @Test
    fun selectedNavigationLabelReservesOtherTouchTargets() {
        for (width in listOf(280f, 320f, 360f, 600f)) {
            val selected = selectedTabWidth(width, 100f)
            assertTrue((width - selected) / 4 >= 48f)
            assertTrue(selected >= width / 5)
        }
        assertTrue(pairFits(328f, 160f, 156f, 12f))
        assertFalse(pairFits(327f, 160f, 156f, 12f))
    }

    @Test
    fun paymentPrefillAndExactCentsDoNotFlashAnUnpaidBalance() {
        for (method in paymentMethods) {
            val fields = PaymentFields(method, 14000.0, emptyList())
            assertTrue(fields.balanced)
            assertEquals(0.0, fields.remaining)
            assertEquals(14000.0, fields.payments.single().total)
        }
        val fields = PaymentFields(CheckoutPaymentMethod.Multiple, .3, emptyList())
        fields[CheckoutPaymentMethod.Cash] = "0.10"
        fields[CheckoutPaymentMethod.Card] = "0.20"
        assertTrue(fields.balanced)
        fields[CheckoutPaymentMethod.Card] = "0.19"
        assertFalse(fields.balanced)
        assertEquals(.01, fields.remaining)
        fields[CheckoutPaymentMethod.Card] = "0.21"
        assertFalse(fields.balanced)
    }

    @Test
    fun existingPaymentsArePreserved() {
        val payments =
            listOf(
                CheckoutPayment(CheckoutPaymentMethod.Cash, 100.0),
                CheckoutPayment(CheckoutPaymentMethod.Card, 150.0),
            )
        val fields = PaymentFields(CheckoutPaymentMethod.Multiple, 250.0, payments)
        assertTrue(fields.balanced)
        assertEquals(payments, fields.payments)
    }

    @Test
    fun completingAnOlderSaleDoesNotClearNewCartItems() {
        val original =
            SelectedPosItem(
                instanceId = "one",
                lassoItem = Product(name = "Labial", price = "250", commissionPercentage = null),
                quantity = 2,
            )
        val added =
            SelectedPosItem(
                instanceId = "two",
                lassoItem = Product(name = "Nuevo", price = "10", commissionPercentage = null),
            )
        val current = listOf(original.copy(quantity = 3), added)
        val remaining = reconcileSubmittedItems(current, listOf(original))
        assertEquals(1, remaining.first().quantity)
        assertEquals(added, remaining.last())
        assertEquals(
            current,
            reconcileSubmittedItems(current, listOf(original.copy(price = 200.0))),
        )
    }
}
