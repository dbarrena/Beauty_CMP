package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.screens.commissions.components.EmployeeSelector
import com.lasso.lassoapp.screens.commissions.dialog.EmployeePickerDialog
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogLayout
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogViewModelV2
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutHeader
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPayment
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPaymentMethod
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.PaymentFields
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.paymentFieldSpec
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckoutSplitPaymentContent(
    totalPrice: Double,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onRegisterSale: (payments: List<CheckoutPayment>) -> Unit,
    onSelectEmployee: (Employee) -> Unit,
    modifier: Modifier = Modifier,
    checkoutPaymentMethod: CheckoutPaymentMethod,
    state: CheckoutDialogViewModelV2.CheckoutDialogState,
    title: String = "Registrar Venta",
    showEmployeeSelector: Boolean = true,
    initialPayments: List<CheckoutPayment> = emptyList(),
) {
    val fields =
        remember(initialPayments, checkoutPaymentMethod, totalPrice) {
            PaymentFields(checkoutPaymentMethod, totalPrice, initialPayments)
        }
    var showEmployeePicker by remember { mutableStateOf(false) }
    CheckoutDialogLayout(
        onClose = onClose,
        onBack = onBack,
        modifier = modifier,
        header = {
            CheckoutHeader(
                totalPrice,
                title,
                summary =
                    if (fields.remaining > 0.0)
                        ({
                            FlowRow(horizontalArrangement = Arrangement.Center) {
                                Text("Restan ", color = LassoTextMuted, fontSize = 18.sp)
                                Text(
                                    "$${fields.remaining.toPosMoneyString()}",
                                    color = LassoSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        })
                    else null,
            )
        },
        actions = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onRegisterSale(fields.payments) },
                    enabled = fields.balanced && !state.isLoading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = LassoPrimary,
                            contentColor = Color.White,
                        ),
                ) {
                    Text(
                        if (state.isLoading) "Registrando…"
                        else "Confirmar pago en ${checkoutPaymentMethod.display}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (showEmployeeSelector)
                EmployeeSelector(
                    selectedEmployee = state.selectedEmployee,
                    onClick = { showEmployeePicker = true },
                    enabled = state.canSelectEmployee && !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            fields.methods.forEach { method ->
                val spec = paymentFieldSpec(method)
                SplitPaymentRow(
                    spec.label,
                    spec.circleColor,
                    spec.icon,
                    fields[method],
                    { fields[method] = it },
                )
            }
        }
    }
    EmployeePickerDialog(
        isVisible = showEmployeeSelector && showEmployeePicker,
        employees = state.employees,
        isLoading = state.isLoading,
        onDismiss = { showEmployeePicker = false },
    ) {
        onSelectEmployee(it)
        showEmployeePicker = false
    }
}

@Composable
internal fun SplitPaymentRow(
    label: String,
    circleColor: Color,
    icon: DrawableResource,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier.size(CheckoutPaymentMethodTokens.iconCircleSize)
                    .clip(CircleShape)
                    .background(circleColor),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(CheckoutPaymentMethodTokens.iconImageSize),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                color = LassoTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                letterSpacing = (-0.15).sp,
            )
            CheckoutSplitMoneyField(value = value, onValueChange = onValueChange)
        }
    }
}

@Composable
private fun CheckoutSplitMoneyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val textColor = if (value.isEmpty()) LassoTextPlaceholder else LassoTextPrimary
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(shape)
                .background(Color(0xFFF8F8FA))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "$",
                color = LassoTextMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(sanitizeMoneyInput(it)) },
                textStyle =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = textColor,
                        letterSpacing = (-0.31).sp,
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun sanitizeMoneyInput(input: String): String {
    if (input.isEmpty()) return ""
    val sb = StringBuilder()
    var dotSeen = false
    for (c in input) {
        when {
            c.isDigit() -> sb.append(c)
            c == '.' && !dotSeen -> {
                sb.append('.')
                dotSeen = true
            }
        }
    }
    val parts = sb.toString().split('.')
    if (parts.size == 2 && parts[1].length > 2) {
        return parts[0] + "." + parts[1].take(2)
    }
    return sb.toString()
}

private fun parseMoney(s: String): Double = s.replace(",", "").toDoubleOrNull() ?: 0.0
