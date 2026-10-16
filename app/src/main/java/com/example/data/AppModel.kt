package com.example.data

import androidx.compose.ui.graphics.ImageBitmap

enum class AppCategory(val title: String) {
    ALL("All"),
    PRODUCTIVITY("Productivity"),
    SOCIAL("Social"),
    MULTIMEDIA("Multimedia"),
    GAMES("Games"),
    TOOLS("Tools"),
    OTHER("Other")
}

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
    val category: AppCategory = AppCategory.OTHER,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L
)

data class SmartFolder(
    val id: String,
    val name: String,
    val appPackageNames: List<String>,
    val category: AppCategory = AppCategory.OTHER
)
