// Path: app/src/main/java/com/laddu/os/launcher/MainActivity.kt
package com.laddu.os.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: LauncherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LauncherViewModel(applicationContext) as T
            }
        })[LauncherViewModel::class.java]

        setContent {
            val currentTheme by viewModel.currentTheme.collectAsState()
            val isAppDrawerOpen by viewModel.isAppDrawerOpen.collectAsState()
            var isSettingsOpen by remember { mutableStateOf(false) }

            LadduOSTheme(theme = currentTheme) {
                Box(modifier = Modifier.fillMaxSize().background(getWallpaperGradient(currentTheme))) {
                    HomeScreen(viewModel = viewModel, theme = currentTheme, onOpenSettings = { isSettingsOpen = true })
                    AnimatedVisibility(visible = isAppDrawerOpen, enter = fadeIn(), exit = fadeOut()) {
                        AppDrawerScreen(viewModel = viewModel, theme = currentTheme, onBack = { viewModel.toggleAppDrawer(false) })
                    }
                    if (isSettingsOpen) {
                        ThemeSettingsSheet(viewModel = viewModel, currentTheme = currentTheme, onDismiss = { isSettingsOpen = false })
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isAppDrawerOpen.value) {
            viewModel.toggleAppDrawer(false)
        } else {
            viewModel.performHapticFeedback()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInstalledApplications()
    }
}
