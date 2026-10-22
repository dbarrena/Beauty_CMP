package com.lasso.lassoapp.screens.register

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.ui.theme.LightLassoColorScheme
import com.lasso.lassoapp.ui.theme.lassoTypography
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.lasso_icon_full_cropped
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(onLogin: () -> Unit) {
    val viewModel = koinViewModel<RegisterScreenViewModel>()
    val state by viewModel.state.collectAsState()
    RegisterScreenContent(state, viewModel::updateField, viewModel::register, onLogin)
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RegisterScreenContent(
    state: RegisterScreenState,
    onFieldChange: (RegistrationField, String) -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    var footerHeightPx by remember { mutableStateOf(0) }
    val headerSpacing = 40.dp
    // The footer and card margins already occupy part of the keyboard's inset.
    val formKeyboardInsets = WindowInsets.ime.exclude(
        WindowInsets(bottom = 24.dp + 4.dp + with(density) { footerHeightPx.toDp() }),
    )
    val submit = {
        keyboard?.hide()
        focusManager.clearFocus()
        onRegister()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(LassoPrimary),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.lasso_icon_full_cropped),
                contentDescription = "LASSO · Une, controla, crece",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.padding(top = headerSpacing, bottom = headerSpacing).width(220.dp),
            )
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                        .animateContentSize(
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                            alignment = Alignment.TopCenter,
                        )
                        .then(if (state.isRegistered) Modifier.wrapContentHeight() else Modifier.fillMaxHeight()),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .then(if (state.isRegistered) Modifier else Modifier.fillMaxHeight())
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp,
                                bottom = if (state.isRegistered) 24.dp else 4.dp),
                        horizontalAlignment = if (state.isRegistered) Alignment.CenterHorizontally else Alignment.Start,
                    ) {
                        Text(
                            text = if (state.isRegistered) "¡Cuenta creada!" else "Crear cuenta",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold, fontSize = 24.sp, color = LassoTextPrimary,
                            ),
                            textAlign = if (state.isRegistered) TextAlign.Center else TextAlign.Start,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (state.isRegistered) "Ya puedes iniciar sesión con tu correo y contraseña."
                                else "Configura tu cuenta en LASSO",
                            style = MaterialTheme.typography.bodyMedium.copy(color = LassoTextMuted),
                            textAlign = if (state.isRegistered) TextAlign.Center else TextAlign.Start,
                        )
                        Spacer(Modifier.height(24.dp))
                        if (state.isRegistered) {
                            Button(
                                onClick = onLogin,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary, contentColor = Color.White),
                            ) { Text("Iniciar sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
                        } else {
                            Column(
                                Modifier.fillMaxWidth().weight(1f).windowInsetsPadding(formKeyboardInsets)
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                RegistrationField.entries.forEach { field ->
                                    val (label, value, placeholder) = when (field) {
                                        RegistrationField.CompanyName -> Triple("Nombre de la empresa", state.companyName, "Nombre de tu empresa")
                                        RegistrationField.Name -> Triple("Tu nombre", state.name, "Tu nombre completo")
                                        RegistrationField.Email -> Triple("Correo electrónico", state.email, "nombre@correo.com")
                                        RegistrationField.Password -> Triple("Contraseña", state.password, "Mínimo 8 caracteres")
                                        RegistrationField.ConfirmPassword -> Triple("Confirmar contraseña", state.confirmPassword, "Repite tu contraseña")
                                    }
                                    RegistrationTextField(
                                        label = label, value = value, placeholder = placeholder,
                                        enabled = !state.isLoading,
                                        onValueChange = { onFieldChange(field, it) },
                                        keyboardType = when (field) {
                                            RegistrationField.Email -> KeyboardType.Email
                                            RegistrationField.Password, RegistrationField.ConfirmPassword -> KeyboardType.Password
                                            else -> KeyboardType.Text
                                        },
                                        imeAction = if (field == RegistrationField.ConfirmPassword) ImeAction.Done else ImeAction.Next,
                                        onDone = submit,
                                        error = state.fieldErrors[field],
                                        errorScrollRequest = if (field == state.scrollToField) state.validationAttempt else 0,
                                    )
                                }
                                state.error?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                }
                                Button(
                                    onClick = submit,
                                    enabled = !state.isLoading,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary, contentColor = Color.White),
                                ) {
                                    if (state.isLoading) {
                                        CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text(if (state.isLoading) "Creando cuenta..." else "Crear cuenta",
                                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                }
                            }
                            FlowRow(
                                modifier = Modifier.fillMaxWidth().onSizeChanged { footerHeightPx = it.height },
                                horizontalArrangement = Arrangement.Center,
                                itemVerticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("¿Ya tienes cuenta?", color = LassoTextMuted, fontSize = 12.sp)
                                TextButton(onClick = onLogin, enabled = !state.isLoading) {
                                    Text("Inicia sesión", color = LassoPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RegistrationTextField(
    label: String,
    value: String,
    placeholder: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
    error: String? = null,
    errorScrollRequest: Int = 0,
) {
    var focused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val shape = RoundedCornerShape(20.dp)
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(errorScrollRequest) {
        if (errorScrollRequest > 0) {
            withFrameNanos { }
            bringIntoViewRequester.bringIntoView()
        }
    }
    Column(Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)) {
        Text(label, style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Medium,
            color = if (error != null) MaterialTheme.colorScheme.error else LassoTextPrimary, fontSize = 14.sp,
        ))
        Spacer(Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            isError = error != null,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).onFocusChanged { focused = it.isFocused }
                .then(when {
                    error != null -> Modifier.border(1.dp, MaterialTheme.colorScheme.error, shape)
                    focused -> Modifier.border(1.dp, LassoPrimary, shape)
                    else -> Modifier
                }),
            shape = shape,
            placeholder = { Text(placeholder, color = LassoTextPlaceholder, fontSize = 14.sp) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = LassoSurfaceVariant,
                disabledContainerColor = LassoSurfaceVariant,
                errorContainerColor = LassoSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = LassoPrimary,
                focusedTextColor = LassoTextPrimary,
                unfocusedTextColor = LassoTextPrimary,
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation()
                else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { onDone() },
            ),
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
        RegisterScreenContent(RegisterScreenState(), { _, _ -> }, {}, {})
    }
}

@Preview
@Composable
private fun RegistrationSuccessPreview() {
    MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
        RegisterScreenContent(RegisterScreenState(isRegistered = true), { _, _ -> }, {}, {})
    }
}
