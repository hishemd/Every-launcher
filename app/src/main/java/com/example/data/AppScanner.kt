package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppScanner {

    suspend fun getInstalledApps(context: Context): List<AppItem> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val myPackageName = context.packageName
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        } catch (_: Exception) {
            try {
                packageManager.queryIntentActivities(intent, 0)
            } catch (_: Exception) {
                emptyList()
            }
        }

        val appList = mutableListOf<AppItem>()

        for (resolveInfo in resolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            // Skip the launcher itself
            if (pkg == myPackageName) continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name

            val isSystem = try {
                val appInfo = packageManager.getApplicationInfo(pkg, 0)
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } catch (_: Exception) {
                false
            }

            val iconDrawable = try {
                resolveInfo.loadIcon(packageManager)
            } catch (_: Exception) {
                null
            }

            val iconBitmap = iconDrawable?.let { drawableToBitmap(it)?.asImageBitmap() }
            val category = categorizeApp(pkg, label)

            appList.add(
                AppItem(
                    packageName = pkg,
                    activityName = activityName,
                    label = label,
                    iconBitmap = iconBitmap,
                    category = category,
                    isSystemApp = isSystem
                )
            )
        }

        // Sort alphabetically
        appList.sortBy { it.label.lowercase() }

        // If no apps found (e.g. strict emulator sandbox without apps), provide authentic OneUI default apps
        if (appList.isEmpty()) {
            return@withContext getFallbackOneUiApps()
        }

        appList
    }

    private fun categorizeApp(packageName: String, label: String): AppCategory {
        val text = "${packageName.lowercase()} ${label.lowercase()}"
        return when {
            text.contains("game") || text.contains("supercell") || text.contains("unity") || text.contains("minecraft") || text.contains("play.games") -> AppCategory.GAMES
            text.contains("message") || text.contains("whatsapp") || text.contains("telegram") || text.contains("social") ||
                    text.contains("facebook") || text.contains("instagram") || text.contains("twitter") || text.contains("tiktok") ||
                    text.contains("reddit") || text.contains("chat") || text.contains("contact") || text.contains("dialer") || text.contains("phone") -> AppCategory.SOCIAL
            text.contains("music") || text.contains("spotify") || text.contains("youtube") || text.contains("camera") ||
                    text.contains("gallery") || text.contains("photo") || text.contains("video") || text.contains("audio") ||
                    text.contains("sound") || text.contains("netflix") || text.contains("recorder") -> AppCategory.MULTIMEDIA
            text.contains("calendar") || text.contains("note") || text.contains("office") || text.contains("docs") ||
                    text.contains("sheet") || text.contains("mail") || text.contains("gmail") || text.contains("calculator") ||
                    text.contains("task") || text.contains("keep") || text.contains("word") -> AppCategory.PRODUCTIVITY
            text.contains("setting") || text.contains("clock") || text.contains("file") || text.contains("download") ||
                    text.contains("browser") || text.contains("chrome") || text.contains("weather") || text.contains("vending") ||
                    text.contains("store") || text.contains("tool") || text.contains("map") -> AppCategory.TOOLS
            else -> AppCategory.OTHER
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return try {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                drawable.bitmap
            } else {
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 144
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 144
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun getFallbackOneUiApps(): List<AppItem> {
        return listOf(
            AppItem("com.samsung.android.dialer", "", "Phone", null, AppCategory.SOCIAL, true),
            AppItem("com.samsung.android.messaging", "", "Messages", null, AppCategory.SOCIAL, true),
            AppItem("com.sec.android.app.camera", "", "Camera", null, AppCategory.MULTIMEDIA, true),
            AppItem("com.sec.android.gallery3d", "", "Gallery", null, AppCategory.MULTIMEDIA, true),
            AppItem("com.android.settings", "", "Settings", null, AppCategory.TOOLS, true),
            AppItem("com.sec.android.app.sbrowser", "", "Internet", null, AppCategory.TOOLS, true),
            AppItem("com.sec.android.app.clockpackage", "", "Clock", null, AppCategory.TOOLS, true),
            AppItem("com.samsung.android.calendar", "", "Calendar", null, AppCategory.PRODUCTIVITY, true),
            AppItem("com.sec.android.app.popupcalculator", "", "Calculator", null, AppCategory.PRODUCTIVITY, true),
            AppItem("com.samsung.android.app.notes", "", "Notes", null, AppCategory.PRODUCTIVITY, true),
            AppItem("com.sec.android.daemonapp", "", "Weather", null, AppCategory.TOOLS, true),
            AppItem("com.sec.android.app.music", "", "Music", null, AppCategory.MULTIMEDIA, true),
            AppItem("com.samsung.android.app.contacts", "", "Contacts", null, AppCategory.SOCIAL, true),
            AppItem("com.samsung.android.app.myfiles", "", "My Files", null, AppCategory.TOOLS, true)
        )
    }
}
