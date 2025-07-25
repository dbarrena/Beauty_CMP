package com.beauty.beautyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Remove when https://issuetracker.google.com/issues/364713509 is fixed
            /*LaunchedEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
            }*/
            val systemUiController = rememberSystemUiController()
            val isDarkTheme = isSystemInDarkTheme()
            val useDarkIcons = !isDarkTheme // true = black icons for light backgrounds
            val statusBarColor = MaterialTheme.colorScheme.primary // pick your background

            SideEffect {
                systemUiController.setStatusBarColor(
                    color = statusBarColor,
                    darkIcons = true
                )
            }
            App()
        }
    }
}
