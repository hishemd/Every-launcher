package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class LauncherRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("one_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SETTINGS_JSON = "key_settings_json"
        private const val KEY_FOLDERS_JSON = "key_folders_json"
        private const val KEY_RECENT_APPS = "key_recent_apps"
        private const val KEY_FAVORITES_DOCK = "key_favorites_dock"
    }

    fun loadSettings(): LauncherSettings {
        val jsonStr = prefs.getString(KEY_SETTINGS_JSON, null) ?: return defaultSettings()
        return try {
            val json = JSONObject(jsonStr)
            val weatherJson = json.optJSONObject("weather") ?: JSONObject()
            val dockArray = json.optJSONArray("dockPackages") ?: JSONArray()
            val dockList = mutableListOf<String>()
            for (i in 0 until dockArray.length()) {
                dockList.add(dockArray.getString(i))
            }

            LauncherSettings(
                theme = ThemePreference.valueOf(json.optString("theme", ThemePreference.DARK.name)),
                wallpaper = WallpaperOption.valueOf(json.optString("wallpaper", WallpaperOption.GALAXY_NEBULA.name)),
                iconSize = IconSizeOption.valueOf(json.optString("iconSize", IconSizeOption.MEDIUM.name)),
                gridSize = GridSizeOption.valueOf(json.optString("gridSize", GridSizeOption.GRID_4X5.name)),
                showLabels = json.optBoolean("showLabels", true),
                animationSpeedMultiplier = json.optDouble("animationSpeedMultiplier", 1.0).toFloat(),
                parallaxEnabled = json.optBoolean("parallaxEnabled", true),
                doubleTapGesture = GestureAction.valueOf(json.optString("doubleTapGesture", GestureAction.LOCK_PREVIEW.name)),
                swipeUpGesture = GestureAction.valueOf(json.optString("swipeUpGesture", GestureAction.OPEN_DRAWER.name)),
                swipeDownGesture = GestureAction.valueOf(json.optString("swipeDownGesture", GestureAction.OPEN_NOTIFICATIONS.name)),
                longPressGesture = GestureAction.valueOf(json.optString("longPressGesture", GestureAction.OPEN_SETTINGS.name)),
                dockPackages = if (dockList.isEmpty()) defaultDockPackages() else dockList,
                weather = WeatherConfig(
                    city = weatherJson.optString("city", "Seoul, KR"),
                    temperature = weatherJson.optInt("temperature", 22),
                    condition = weatherJson.optString("condition", "Partly Cloudy"),
                    highTemp = weatherJson.optInt("highTemp", 26),
                    lowTemp = weatherJson.optInt("lowTemp", 18),
                    unit = weatherJson.optString("unit", "°C"),
                    isDemo = weatherJson.optBoolean("isDemo", true)
                )
            )
        } catch (_: Exception) {
            defaultSettings()
        }
    }

    fun saveSettings(settings: LauncherSettings) {
        val json = JSONObject().apply {
            put("theme", settings.theme.name)
            put("wallpaper", settings.wallpaper.name)
            put("iconSize", settings.iconSize.name)
            put("gridSize", settings.gridSize.name)
            put("showLabels", settings.showLabels)
            put("animationSpeedMultiplier", settings.animationSpeedMultiplier.toDouble())
            put("parallaxEnabled", settings.parallaxEnabled)
            put("doubleTapGesture", settings.doubleTapGesture.name)
            put("swipeUpGesture", settings.swipeUpGesture.name)
            put("swipeDownGesture", settings.swipeDownGesture.name)
            put("longPressGesture", settings.longPressGesture.name)
            val dockArray = JSONArray()
            settings.dockPackages.forEach { dockArray.put(it) }
            put("dockPackages", dockArray)
            val weatherJson = JSONObject().apply {
                put("city", settings.weather.city)
                put("temperature", settings.weather.temperature)
                put("condition", settings.weather.condition)
                put("highTemp", settings.weather.highTemp)
                put("lowTemp", settings.weather.lowTemp)
                put("unit", settings.weather.unit)
                put("isDemo", settings.weather.isDemo)
            }
            put("weather", weatherJson)
        }
        prefs.edit().putString(KEY_SETTINGS_JSON, json.toString()).apply()
    }

    fun loadFolders(installedApps: List<AppItem>): List<SmartFolder> {
        val jsonStr = prefs.getString(KEY_FOLDERS_JSON, null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<SmartFolder>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val catName = obj.optString("category", AppCategory.OTHER.name)
                    val cat = try { AppCategory.valueOf(catName) } catch (_: Exception) { AppCategory.OTHER }
                    val pkgsArray = obj.getJSONArray("apps")
                    val pkgs = mutableListOf<String>()
                    for (j in 0 until pkgsArray.length()) {
                        pkgs.add(pkgsArray.getString(j))
                    }
                    list.add(SmartFolder(id = id, name = name, appPackageNames = pkgs, category = cat))
                }
                if (list.isNotEmpty()) return list
            } catch (_: Exception) {
                // fall through to default
            }
        }
        return generateDefaultFolders(installedApps)
    }

    fun saveFolders(folders: List<SmartFolder>) {
        val array = JSONArray()
        for (folder in folders) {
            val obj = JSONObject()
            obj.put("id", folder.id)
            obj.put("name", folder.name)
            obj.put("category", folder.category.name)
            val pkgsArray = JSONArray()
            folder.appPackageNames.forEach { pkgsArray.put(it) }
            obj.put("apps", pkgsArray)
            array.put(obj)
        }
        prefs.edit().putString(KEY_FOLDERS_JSON, array.toString()).apply()
    }

    fun loadRecentApps(): List<String> {
        val set = prefs.getString(KEY_RECENT_APPS, "") ?: ""
        if (set.isBlank()) return emptyList()
        return set.split(",").filter { it.isNotBlank() }.take(5)
    }

    fun recordAppLaunch(packageName: String) {
        val current = loadRecentApps().toMutableList()
        current.remove(packageName)
        current.add(0, packageName)
        prefs.edit().putString(KEY_RECENT_APPS, current.take(8).joinToString(",")).apply()
    }

    fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("settings", JSONObject(prefs.getString(KEY_SETTINGS_JSON, "{}") ?: "{}"))
        root.put("folders", JSONArray(prefs.getString(KEY_FOLDERS_JSON, "[]") ?: "[]"))
        return root.toString(2)
    }

    fun importBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.has("settings")) {
                prefs.edit().putString(KEY_SETTINGS_JSON, root.getJSONObject("settings").toString()).apply()
            }
            if (root.has("folders")) {
                prefs.edit().putString(KEY_FOLDERS_JSON, root.getJSONArray("folders").toString()).apply()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    private fun defaultSettings() = LauncherSettings(
        dockPackages = defaultDockPackages()
    )

    private fun defaultDockPackages(): List<String> = listOf(
        "com.samsung.android.dialer",
        "com.samsung.android.messaging",
        "com.sec.android.app.sbrowser",
        "com.sec.android.app.camera"
    )

    private fun generateDefaultFolders(apps: List<AppItem>): List<SmartFolder> {
        val productivityApps = apps.filter { it.category == AppCategory.PRODUCTIVITY }.map { it.packageName }
        val socialApps = apps.filter { it.category == AppCategory.SOCIAL }.map { it.packageName }
        val mediaApps = apps.filter { it.category == AppCategory.MULTIMEDIA }.map { it.packageName }
        val toolsApps = apps.filter { it.category == AppCategory.TOOLS }.map { it.packageName }
        val gamesApps = apps.filter { it.category == AppCategory.GAMES }.map { it.packageName }

        return listOf(
            SmartFolder("f_prod", "Productivity", productivityApps.take(9), AppCategory.PRODUCTIVITY),
            SmartFolder("f_social", "Social & Chat", socialApps.take(9), AppCategory.SOCIAL),
            SmartFolder("f_media", "Multimedia", mediaApps.take(9), AppCategory.MULTIMEDIA),
            SmartFolder("f_tools", "Tools & System", toolsApps.take(9), AppCategory.TOOLS),
            SmartFolder("f_games", "Games & Fun", gamesApps.take(9), AppCategory.GAMES)
        )
    }
}
