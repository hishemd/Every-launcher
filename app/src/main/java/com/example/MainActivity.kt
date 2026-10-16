package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ThemePreference
import com.example.ui.LauncherScreen
import com.example.ui.LauncherViewModel
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreenPreview
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GalaxyMidnight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: LauncherViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDark = when (uiState.settings.theme) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GalaxyMidnight
                ) {
                    AnimatedContent(
                        targetState = uiState.currentScreen,
                        transitionSpec = {
                            when {
                                targetState == LauncherScreen.APP_DRAWER -> {
                                    (slideInVertically { it / 2 } + fadeIn()) togetherWith (fadeOut())
                                }
                                initialState == LauncherScreen.APP_DRAWER -> {
                                    fadeIn() togetherWith (slideOutVertically { it / 2 } + fadeOut())
                                }
                                targetState == LauncherScreen.LOCK_PREVIEW -> {
                                    fadeIn() togetherWith fadeOut()
                                }
                                else -> {
                                    fadeIn() togetherWith fadeOut()
                                }
                            }
                        },
                        label = "LauncherScreenTransition"
                    ) { screen ->
                        when (screen) {
                            LauncherScreen.HOME -> {
                                BackHandler {
                                    // Home screen consumes back button
                                }
                                HomeScreen(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }
                            LauncherScreen.APP_DRAWER -> {
                                AppDrawerScreen(
                                    allApps = uiState.installedApps,
                                    recentApps = uiState.recentApps,
                                    settings = uiState.settings,
                                    onLaunchApp = { viewModel.launchApp(this@MainActivity, it) },
                                    onPinToDock = { viewModel.pinToDock(it) },
                                    onCloseDrawer = { viewModel.setScreen(LauncherScreen.HOME) }
                                )
                            }
                            LauncherScreen.SETTINGS -> {
                                SettingsScreen(
                                    currentSettings = uiState.settings,
                                    onUpdateSettings = { viewModel.updateSettings(it) },
                                    onExportBackup = { viewModel.exportBackup() },
                                    onImportBackup = { viewModel.importBackup(it) },
                                    onResetDefaults = { viewModel.resetDefaults() },
                                    onCloseSettings = { viewModel.setScreen(LauncherScreen.HOME) }
                                )
                            }
                            LauncherScreen.LOCK_PREVIEW -> {
                                LockScreenPreview(
                                    wallpaper = uiState.settings.wallpaper,
                                    onClosePreview = { viewModel.setScreen(LauncherScreen.HOME) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
