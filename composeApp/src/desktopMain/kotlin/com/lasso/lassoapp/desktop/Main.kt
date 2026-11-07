package com.lasso.lassoapp.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lasso.lassoapp.di.initKoin
import java.awt.Dimension
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.lasso_app_icon
import org.jetbrains.compose.resources.painterResource
import com.lasso.lassoapp.ui.theme.LassoPageBackground

fun main() {
    val isMac = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
    if (isMac) {
        // Set before AWT initializes so native title text contrasts with our light background.
        System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
        System.setProperty("apple.awt.application.name", "Lasso")
    }
    initKoin()
    application {
        val windowState = rememberWindowState(width = 1440.dp, height = 900.dp)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Lasso",
            icon = painterResource(Res.drawable.lasso_app_icon),
            state = windowState,
        ) {
            window.minimumSize = Dimension(1000, 680)
            DisposableEffect(window) {
                if (isMac) {
                    window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                    window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                    window.rootPane.putClientProperty("apple.awt.windowTitleVisible", true)
                    window.background = java.awt.Color(0xF8, 0xF8, 0xFA)
                }
                val listener = installWindowsTitleBarColors(window)
                onDispose { window.removeWindowListener(listener) }
            }
            // Full window content paints the macOS title area too. Keep app controls below it.
            Box(Modifier.fillMaxSize().background(LassoPageBackground).padding(
                top = if (isMac && windowState.placement != WindowPlacement.Fullscreen) 32.dp else 0.dp,
            )) {
                DesktopApp()
            }
        }
    }
}
