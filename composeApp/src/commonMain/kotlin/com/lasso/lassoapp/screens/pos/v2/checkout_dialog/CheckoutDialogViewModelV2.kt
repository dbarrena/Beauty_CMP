package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Payment
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.model.SaleDetail
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal interface CheckoutRepository {
    suspend fun employees(): List<Employee>

    suspend fun employeeId(): Int?

    suspend fun submit(sale: Sale)
}

class CheckoutDialogViewModelV2 internal constructor(private val repository: CheckoutRepository) :
    ViewModel() {
    constructor(
        api: LassoApi,
        session: SessionRepository,
    ) : this(
        object : CheckoutRepository {
            override suspend fun employees() = api.getEmployees()

            override suspend fun employeeId() = session.getEmployeeId()

            override suspend fun submit(sale: Sale) {
                api.registerSale(sale)
            }
        }
    )

    private val _state = MutableStateFlow(CheckoutDialogState())
    val state = _state.asStateFlow()
    private var optionsJob: Job? = null
    private var resumeSubmission = false
    private var nextCompletionId = 0L

    /** Visibility is separate from request lifetime. Never reset a pending or failed submission. */
    fun beginCheckout() {
        if (resumeSubmission || _state.value.isSubmitting || _state.value.completion != null) return
        optionsJob?.cancel()
        _state.value = CheckoutDialogState(isLoading = true)
        optionsJob =
            viewModelScope.launch {
                try {
                    val employees = repository.employees()
                    val employeeId = repository.employeeId()
                    _state.update {
                        it.copy(
                            employees = employees,
                            selectedEmployee =
                                employees.find { employee -> employee.id == employeeId }
                                    ?: employees.firstOrNull(),
                            canSelectEmployee = true,
                            isLoading = false,
                        )
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            }
    }

    fun navigateToMethodPicker() = navigate(CheckoutStep.MethodPicker)

    fun navigateToSplitPayment(method: CheckoutPaymentMethod) =
        navigate(CheckoutStep.SplitPayment(method))

    fun navigateToRegisterDiscount() = navigate(CheckoutStep.Discount)

    private fun navigate(step: CheckoutStep) {
        if (!_state.value.isLoading) {
            resumeSubmission = false
            _state.update {
                it.copy(
                    step = step,
                    error = null,
                    draftPayments = emptyList(),
                    checkoutTotal = null,
                )
            }
        }
    }

    fun registerDiscount(amount: Double) {
        if (!_state.value.isLoading)
            _state.update {
                it.copy(
                    step = CheckoutStep.MethodPicker,
                    discountAmount = amount.takeIf { value -> value > 0.0 },
                    error = null,
                )
            }
    }

    fun setSelectedEmployee(employee: Employee) {
        if (!_state.value.isSubmitting) _state.update { it.copy(selectedEmployee = employee) }
    }

    /** A completed cart is reconciled by the screen even if this dialog is currently hidden. */
    fun acknowledgeCompletion(id: Long, dialogVisible: Boolean) {
        if (_state.value.completion?.id != id) return
        _state.update { it.copy(completion = null) }
        if (!dialogVisible) resumeSubmission = false
    }

    fun dismissCheckout() {
        if (_state.value.step is CheckoutStep.Success) resumeSubmission = false
    }

    fun registerSale(items: List<SelectedPosItem>, unprocessedPayments: List<CheckoutPayment>) {
        val current = _state.value
        if (current.isLoading || current.step is CheckoutStep.Success || items.isEmpty()) return
        val snapshot = items.map { it.copy() }
        val payments = unprocessedPayments.map { it.copy() }
        val sale =
            Sale(
                saleDetails =
                    snapshot.map {
                        SaleDetail(
                            itemType = it.lassoItem.type,
                            itemId = it.lassoItem.id ?: 0,
                            quantity = it.quantity,
                            price = it.price,
                            employeeId = current.selectedEmployee?.id,
                        )
                    },
                payments =
                    payments.map { Payment(total = it.total, paymentType = it.paymentType.key) },
                discountAmount = current.discountAmount,
            )
        resumeSubmission = true
        _state.update {
            it.copy(
                isLoading = true,
                isSubmitting = true,
                error = null,
                draftPayments = payments,
                checkoutTotal = snapshot.sumOf { item -> item.price * item.quantity },
            )
        }
        viewModelScope.launch {
            try {
                repository.submit(sale)
                val completion = CheckoutCompletion(++nextCompletionId, snapshot)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSubmitting = false,
                        error = null,
                        step = CheckoutStep.Success(payments.sumOf { payment -> payment.total }),
                        completion = completion,
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _state.update {
                    it.copy(isLoading = false, isSubmitting = false, error = error.message)
                }
            }
        }
    }

    data class CheckoutCompletion(val id: Long, val items: List<SelectedPosItem>)

    data class CheckoutDialogState(
        val step: CheckoutStep = CheckoutStep.MethodPicker,
        val total: Double = 0.0,
        val remainingTotal: Double = 0.0,
        val isLoading: Boolean = false,
        val isSubmitting: Boolean = false,
        val error: String? = null,
        val employees: List<Employee> = emptyList(),
        val selectedEmployee: Employee? = null,
        val canSelectEmployee: Boolean = false,
        val discountAmount: Double? = null,
        val checkoutTotal: Double? = null,
        val draftPayments: List<CheckoutPayment> = emptyList(),
        val completion: CheckoutCompletion? = null,
    )
}
