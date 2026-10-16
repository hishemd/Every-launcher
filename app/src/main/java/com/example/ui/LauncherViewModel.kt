package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppItem
import com.example.data.AppScanner
import com.example.data.GestureAction
import com.example.data.LauncherRepository
import com.example.data.LauncherSettings
import com.example.data.SmartFolder
import com.example.data.WeatherConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LauncherScreen {
    HOME,
    APP_DRAWER,
    SETTINGS,
    LOCK_PREVIEW
}

data class LauncherUiState(
    val currentScreen: LauncherScreen = LauncherScreen.HOME,
    val installedApps: List<AppItem> = emptyList(),
    val recentApps: List<AppItem> = emptyList(),
    val dockApps: List<AppItem> = emptyList(),
    val folders: List<SmartFolder> = emptyList(),
    val settings: LauncherSettings = LauncherSettings(),
    val activeFolder: SmartFolder? = null,
    val isLoading: Boolean = true
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherRepository(application)

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val apps = AppScanner.getInstalledApps(getApplication())
            val settings = repository.loadSettings()
            val folders = repository.loadFolders(apps)
            val recentPkgList = repository.loadRecentApps()

            val recentAppItems = recentPkgList.mapNotNull { pkg ->
                apps.find { it.packageName == pkg }
            }

            val dockAppItems = resolveDockApps(apps, settings.dockPackages)

            _uiState.update {
                it.copy(
                    installedApps = apps,
                    recentApps = recentAppItems,
                    dockApps = dockAppItems,
                    folders = folders,
                    settings = settings,
                    isLoading = false
                )
            }
        }
    }

    private fun resolveDockApps(apps: List<AppItem>, dockPackages: List<String>): List<AppItem> {
        val found = mutableListOf<AppItem>()
        dockPackages.forEach { pkg ->
            apps.find { it.packageName == pkg }?.let { found.add(it) }
        }
        if (found.size < 4) {
            // Fill with default popular apps
            val defaults = apps.take(4)
            return (found + defaults).distinctBy { it.packageName }.take(4)
        }
        return found.take(4)
    }

    fun setScreen(screen: LauncherScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun launchApp(context: Context, app: AppItem) {
        repository.recordAppLaunch(app.packageName)
        refreshRecentApps()

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null) {
            try {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return
            } catch (_: Exception) {}
        }

        // Smart system intent fallback for authentic OneUI built-in apps
        val fallbackIntent = when {
            app.packageName.contains("dialer") || app.label.equals("Phone", true) ->
                Intent(Intent.ACTION_DIAL)
            app.packageName.contains("message") || app.label.equals("Messages", true) ->
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)
            app.packageName.contains("camera") || app.label.equals("Camera", true) ->
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            app.packageName.contains("gallery") || app.label.equals("Gallery", true) ->
                Intent(Intent.ACTION_VIEW).setType("image/*")
            app.packageName.contains("setting") || app.label.equals("Settings", true) ->
                Intent(Settings.ACTION_SETTINGS)
            app.packageName.contains("browser") || app.label.equals("Internet", true) ->
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            app.packageName.contains("clock") || app.label.equals("Clock", true) ->
                Intent(AlarmClock.ACTION_SHOW_ALARMS)
            app.packageName.contains("calendar") || app.label.equals("Calendar", true) ->
                Intent(Intent.ACTION_VIEW).setData(CalendarContract.CONTENT_URI)
            app.packageName.contains("calculator") || app.label.equals("Calculator", true) ->
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR)
            else -> null
        }

        if (fallbackIntent != null) {
            try {
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "Opening ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Opening ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshRecentApps() {
        val apps = _uiState.value.installedApps
        val recentPkgList = repository.loadRecentApps()
        val recentAppItems = recentPkgList.mapNotNull { pkg ->
            apps.find { it.packageName == pkg }
        }
        _uiState.update { it.copy(recentApps = recentAppItems) }
    }

    fun openSearch(context: Context) {
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(searchIntent)
        } catch (_: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "Search unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun expandNotificationShade(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod("expandNotificationsPanel")
            expand.invoke(statusBarService)
        } catch (_: Exception) {
            // Alternatively fallback to Quick Settings action or prompt
            Toast.makeText(context, "Notification shade gesture activated", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleGesture(action: GestureAction, context: Context) {
        when (action) {
            GestureAction.NONE -> {}
            GestureAction.OPEN_DRAWER -> setScreen(LauncherScreen.APP_DRAWER)
            GestureAction.OPEN_SETTINGS -> setScreen(LauncherScreen.SETTINGS)
            GestureAction.LOCK_PREVIEW -> setScreen(LauncherScreen.LOCK_PREVIEW)
            GestureAction.OPEN_SEARCH -> openSearch(context)
            GestureAction.OPEN_NOTIFICATIONS -> expandNotificationShade(context)
        }
    }

    fun updateSettings(newSettings: LauncherSettings) {
        repository.saveSettings(newSettings)
        val dockAppItems = resolveDockApps(_uiState.value.installedApps, newSettings.dockPackages)
        _uiState.update { it.copy(settings = newSettings, dockApps = dockAppItems) }
    }

    fun updateWeather(newWeather: WeatherConfig) {
        val updated = _uiState.value.settings.copy(weather = newWeather)
        updateSettings(updated)
    }

    fun pinToDock(app: AppItem) {
        val currentDock = _uiState.value.settings.dockPackages.toMutableList()
        currentDock.remove(app.packageName)
        currentDock.add(0, app.packageName)
        val updatedSettings = _uiState.value.settings.copy(dockPackages = currentDock.take(4))
        updateSettings(updatedSettings)
        Toast.makeText(getApplication(), "Pinned ${app.label} to dock", Toast.LENGTH_SHORT).show()
    }

    fun openFolder(folder: SmartFolder) {
        _uiState.update { it.copy(activeFolder = folder) }
    }

    fun closeFolder() {
        _uiState.update { it.copy(activeFolder = null) }
    }

    fun renameFolder(folderId: String, newName: String) {
        val currentFolders = _uiState.value.folders.map {
            if (it.id == folderId) it.copy(name = newName) else it
        }
        repository.saveFolders(currentFolders)
        val updatedActive = currentFolders.find { it.id == folderId }
        _uiState.update { it.copy(folders = currentFolders, activeFolder = updatedActive) }
    }

    fun addAppToFolder(folderId: String, packageName: String) {
        val currentFolders = _uiState.value.folders.map { folder ->
            if (folder.id == folderId && !folder.appPackageNames.contains(packageName)) {
                folder.copy(appPackageNames = folder.appPackageNames + packageName)
            } else folder
        }
        repository.saveFolders(currentFolders)
        val updatedActive = currentFolders.find { it.id == folderId }
        _uiState.update { it.copy(folders = currentFolders, activeFolder = updatedActive) }
    }

    fun removeAppFromFolder(folderId: String, packageName: String) {
        val currentFolders = _uiState.value.folders.map { folder ->
            if (folder.id == folderId) {
                folder.copy(appPackageNames = folder.appPackageNames - packageName)
            } else folder
        }
        repository.saveFolders(currentFolders)
        val updatedActive = currentFolders.find { it.id == folderId }
        _uiState.update { it.copy(folders = currentFolders, activeFolder = updatedActive) }
    }

    fun deleteFolder(folderId: String) {
        val currentFolders = _uiState.value.folders.filter { it.id != folderId }
        repository.saveFolders(currentFolders)
        _uiState.update { it.copy(folders = currentFolders, activeFolder = null) }
    }

    fun exportBackup(): String = repository.exportBackupJson()

    fun importBackup(json: String): Boolean {
        val ok = repository.importBackupJson(json)
        if (ok) {
            loadData()
        }
        return ok
    }

    fun resetDefaults() {
        repository.resetToDefaults()
        loadData()
    }
}
