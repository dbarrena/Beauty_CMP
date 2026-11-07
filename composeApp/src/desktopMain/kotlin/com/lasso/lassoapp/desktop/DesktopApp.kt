package com.lasso.lassoapp.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.screens.login.LoginScreen
import com.lasso.lassoapp.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.FilterQuality
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.lasso_desktop_login
import org.jetbrains.compose.resources.imageResource

@Composable
fun DesktopApp() {
    MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
        val repository = koinInject<SessionRepository>()
        val scope = rememberCoroutineScope()
        var ready by remember { mutableStateOf(false) }
        var session by remember { mutableStateOf<Session?>(null) }
        var sessionError by remember { mutableStateOf<String?>(null) }
        var attempt by remember { mutableIntStateOf(0) }
        LaunchedEffect(attempt) {
            try {
                session = repository.getSession()?.takeIf { it.token.isNotBlank() }
                sessionError = null
                ready = true
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (exception: Exception) {
                sessionError = exception.message ?: "No se pudo cargar la sesión."
            }
        }
        Surface(Modifier.fillMaxSize(), color = LassoPageBackground) {
            if (!ready) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (sessionError == null) CircularProgressIndicator() else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(sessionError.orEmpty(), color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { attempt++ }) { Text("Reintentar") }
                    }
                }
            } else key(session?.partnerId, session?.employeeId) {
                // A fresh store on login/logout disposes all prior account state and pending jobs.
                val owner = remember { object : ViewModelStoreOwner { override val viewModelStore = ViewModelStore() } }
                DisposableEffect(owner) { onDispose { owner.viewModelStore.clear() } }
                CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                    val currentSession = session
                    if (currentSession == null) {
                        Box(Modifier.fillMaxSize().background(LassoPageBackground), contentAlignment = Alignment.Center) {
                            Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp), color = LassoSurface,
                                modifier = Modifier.width(460.dp).fillMaxHeight(.88f)) {
                                LoginScreen(onRegister = {}, showRegistration = false,
                                    logo = { modifier -> Image(imageResource(Res.drawable.lasso_desktop_login),
                                        "Lasso Logo", modifier, filterQuality = FilterQuality.Medium) }, onLoginSuccess = {
                                    scope.launch { session = repository.getSession() }
                                })
                            }
                        }
                    } else DesktopCalendarScreen(currentSession, onLogout = {
                        repository.removeSession()
                        session = null
                    })
                }
            }
        }
    }
}
