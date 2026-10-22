package com.lasso.lassoapp.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.lasso_icon_full_cropped
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(onRegister: () -> Unit, onLoginSuccess: () -> Unit) {
    val viewModel = koinViewModel<LoginScreenViewModel>()

    LoginScreenContent(viewModel, onRegister, onLoginSuccess)
}

@Composable
private fun LoginScreenContent(viewModel: LoginScreenViewModel, onRegister: () -> Unit, onLoginSuccess: () -> Unit) {
    val state = viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.lasso_icon_full_cropped),
                contentDescription = "Lasso Logo",
                modifier = Modifier
                    .width(180.dp)
                    .padding(bottom = 40.dp)
            )

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = LassoTextPrimary,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LassoSurfaceVariant,
                        unfocusedContainerColor = LassoSurfaceVariant,
                        disabledContainerColor = LassoSurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = LassoPrimary,
                        focusedTextColor = LassoTextPrimary,
                        unfocusedTextColor = LassoTextPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    enabled = !state.value.isLoading,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = LassoTextPrimary,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LassoSurfaceVariant,
                        unfocusedContainerColor = LassoSurfaceVariant,
                        disabledContainerColor = LassoSurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = LassoPrimary,
                        focusedTextColor = LassoTextPrimary,
                        unfocusedTextColor = LassoTextPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    enabled = !state.value.isLoading,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.login(email.value, password.value, onLoginSuccess)
                },
                enabled = !state.value.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LassoPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = LassoPrimary.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Text(
                    text = if (state.value.isLoading) "Iniciando sesión..." else "Iniciar sesión",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            state.value.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("¿No tienes cuenta? ")
                    withStyle(style = SpanStyle(color = LassoPrimary, fontWeight = FontWeight.Bold)) {
                        append("Regístrate")
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LassoTextMuted,
                    fontSize = 14.sp
                ),
                modifier = Modifier.clickable(enabled = !state.value.isLoading, onClick = onRegister)
            )
        }
    }
}
