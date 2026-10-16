package com.example.data

enum class ThemePreference(val label: String) {
    DARK("Galaxy Dark"),
    LIGHT("Minimal Light"),
    SYSTEM("System Default")
}

enum class WallpaperOption(val label: String, val description: String) {
    GALAXY_NEBULA("Samsung Galaxy Blue", "Luxury flowing cyan & royal blue ribbon"),
    OBSIDIAN_DARK("Obsidian Midnight", "Deep stealth OLED pure dark gradient"),
    CYBER_CYAN("Cyberpunk Glow", "Electric neon cyan and sapphire glow"),
    AURORA_WAVE("Mystic Aurora", "Cosmic deep blue and purple wave"),
    DEEP_SPACE("Cosmic Deep Space", "Infinite stars and midnight blue gradient")
}

enum class IconSizeOption(val label: String, val sizeDp: Int) {
    SMALL("Compact", 48),
    MEDIUM("Standard", 56),
    LARGE("Comfortable", 64)
}

enum class GridSizeOption(val label: String, val columns: Int, val rows: Int) {
    GRID_4X4("4 × 4", 4, 4),
    GRID_4X5("4 × 5", 4, 5),
    GRID_5X5("5 × 5", 5, 5)
}

enum class GestureAction(val label: String) {
    NONE("None"),
    OPEN_DRAWER("Open App Drawer"),
    OPEN_SETTINGS("Launcher Settings"),
    LOCK_PREVIEW("Lock Screen Preview"),
    OPEN_SEARCH("Open Search"),
    OPEN_NOTIFICATIONS("Expand Notification Shade")
}

data class WeatherConfig(
    val city: String = "Seoul, KR",
    val temperature: Int = 22,
    val condition: String = "Partly Cloudy",
    val highTemp: Int = 26,
    val lowTemp: Int = 18,
    val unit: String = "°C",
    val isDemo: Boolean = true
)

data class LauncherSettings(
    val theme: ThemePreference = ThemePreference.DARK,
    val wallpaper: WallpaperOption = WallpaperOption.GALAXY_NEBULA,
    val iconSize: IconSizeOption = IconSizeOption.MEDIUM,
    val gridSize: GridSizeOption = GridSizeOption.GRID_4X5,
    val showLabels: Boolean = true,
    val animationSpeedMultiplier: Float = 1.0f,
    val parallaxEnabled: Boolean = true,
    val doubleTapGesture: GestureAction = GestureAction.LOCK_PREVIEW,
    val swipeUpGesture: GestureAction = GestureAction.OPEN_DRAWER,
    val swipeDownGesture: GestureAction = GestureAction.OPEN_NOTIFICATIONS,
    val longPressGesture: GestureAction = GestureAction.OPEN_SETTINGS,
    val dockPackages: List<String> = emptyList(),
    val weather: WeatherConfig = WeatherConfig()
)
