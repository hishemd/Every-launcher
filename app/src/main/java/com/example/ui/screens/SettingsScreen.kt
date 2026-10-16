package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GestureAction
import com.example.data.GridSizeOption
import com.example.data.IconSizeOption
import com.example.data.LauncherSettings
import com.example.data.ThemePreference
import com.example.data.WallpaperOption
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiBlueLight
import com.example.ui.theme.OneUiCyanGlow

@Composable
fun SettingsScreen(
    currentSettings: LauncherSettings,
    onUpdateSettings: (LauncherSettings) -> Unit,
    onExportBackup: () -> String,
    onImportBackup: (String) -> Boolean,
    onResetDefaults: () -> Unit,
    onCloseSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }
    var showResetConfirm by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    BackHandler { onCloseSettings() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF060914))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onCloseSettings,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "Samsung OneLauncher Pro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Preferences & Customization",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Section 1: Set As Default Launcher Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F1E3D))
                    .border(1.dp, OneUiCyanGlow, RoundedCornerShape(24.dp))
                    .padding(16.dp)
                    .testTag("default_launcher_banner")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OneUiBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Set as Default Android Home", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Experience continuous OneLauncher navigation", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = "To make this your permanent launcher: Open Android Settings → Apps → Default apps → Home app → Select 'Samsung OneLauncher Pro'. Or tap the button below to trigger the system prompt.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                try {
                                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(homeIntent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Open Settings > Apps > Default Apps to choose Home app", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("set_default_home_button")
                    ) {
                        Text("Configure Default Home App in Settings")
                    }
                }
            }

            // Section 2: Wallpaper Selection
            SettingsCard(title = "Wallpaper & Theme", icon = Icons.Default.Palette) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Wallpaper:", color = Color(0xFFCBD5E1), fontSize = 13.sp)

                    WallpaperOption.values().forEach { wp ->
                        val isSelected = currentSettings.wallpaper == wp
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF1E3A8A) else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) OneUiCyanGlow else Color(0xFF334155), RoundedCornerShape(14.dp))
                                .clickable { onUpdateSettings(currentSettings.copy(wallpaper = wp)) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(wp.label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(wp.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = OneUiCyanGlow)
                            }
                        }
                    }

                    // Parallax Wallpaper Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Wallpaper Parallax Depth", color = Color.White, fontSize = 14.sp)
                            Text("Shifts background dynamically with swipe gestures", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = currentSettings.parallaxEnabled,
                            onCheckedChange = { onUpdateSettings(currentSettings.copy(parallaxEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = OneUiCyanGlow, checkedTrackColor = Color(0xFF1E3A8A))
                        )
                    }
                }
            }

            // Section 3: App Grid & Icon Sizing
            SettingsCard(title = "Grid & App Icons", icon = Icons.Default.GridOn) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Grid Size
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Home Screen Grid Size:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GridSizeOption.values().forEach { grid ->
                                val isSelected = currentSettings.gridSize == grid
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) OneUiBlueLight else Color(0xFF1E293B))
                                        .clickable { onUpdateSettings(currentSettings.copy(gridSize = grid)) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(grid.label, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Icon Size
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("App Icon Scale:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconSizeOption.values().forEach { sizeOpt ->
                                val isSelected = currentSettings.iconSize == sizeOpt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) OneUiBlueLight else Color(0xFF1E293B))
                                        .clickable { onUpdateSettings(currentSettings.copy(iconSize = sizeOpt)) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(sizeOpt.label, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Show Icon Labels Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Show App Labels Under Icons", color = Color.White, fontSize = 14.sp)
                        Switch(
                            checked = currentSettings.showLabels,
                            onCheckedChange = { onUpdateSettings(currentSettings.copy(showLabels = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = OneUiCyanGlow, checkedTrackColor = Color(0xFF1E3A8A))
                        )
                    }
                }
            }

            // Section 4: Motion & Animations
            SettingsCard(title = "Animations & Speed", icon = Icons.Default.Animation) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Animation Speed Multiplier:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0.5f to "0.5× Fast", 1.0f to "1.0× Normal", 1.5f to "1.5× Smooth").forEach { (speed, label) ->
                            val isSelected = currentSettings.animationSpeedMultiplier == speed
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) OneUiBlueLight else Color(0xFF1E293B))
                                    .clickable { onUpdateSettings(currentSettings.copy(animationSpeedMultiplier = speed)) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSelected) Color.White else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Section 5: Gestures
            SettingsCard(title = "Gestures & Shortcuts", icon = Icons.Default.TouchApp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GestureSettingItem(
                        title = "Double Tap on Empty Space",
                        currentAction = currentSettings.doubleTapGesture,
                        onSelectAction = { onUpdateSettings(currentSettings.copy(doubleTapGesture = it)) }
                    )
                    GestureSettingItem(
                        title = "Swipe Up on Home",
                        currentAction = currentSettings.swipeUpGesture,
                        onSelectAction = { onUpdateSettings(currentSettings.copy(swipeUpGesture = it)) }
                    )
                    GestureSettingItem(
                        title = "Swipe Down on Home",
                        currentAction = currentSettings.swipeDownGesture,
                        onSelectAction = { onUpdateSettings(currentSettings.copy(swipeDownGesture = it)) }
                    )
                }
            }

            // Section 6: Local Backup & Restore
            SettingsCard(title = "Local Backup & Privacy", icon = Icons.Default.Backup) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "100% Offline & Private: No personal data or telemetry leaves this device.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                exportedJsonText = onExportBackup()
                                showExportDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.weight(1f).testTag("export_backup_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Backup", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.weight(1f).testTag("import_backup_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore Backup", fontSize = 12.sp)
                        }
                    }

                    TextButton(
                        onClick = { showResetConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF43F5E)),
                        modifier = Modifier.align(Alignment.CenterHorizontally).testTag("reset_defaults_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset All to Factory Defaults")
                    }
                }
            }

            // Section 7: APK Build & Export Guide
            SettingsCard(title = "Building & Exporting APK Guide", icon = Icons.Default.Build) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("How to export and install your APK:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("1. In Google AI Studio: Open project menu and select 'Export ZIP' or 'Download APK/AAB'.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text("2. Command line build: Run 'gradle assembleRelease' in project root to generate signed release APK.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text("3. Output artifact: 'app/build/outputs/apk/release/app-release.apk' ready for sideloading or Play Console.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Launcher Backup JSON", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Copy this configuration to restore your folders and layout on any device:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clip.setPrimaryClip(ClipData.newPlainText("OneLauncher Backup", exportedJsonText))
                        Toast.makeText(context, "Copied backup JSON to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight)
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Restore Launcher Settings", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste your backup JSON snippet below:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        placeholder = { Text("Paste JSON here...") },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OneUiCyanGlow,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = onImportBackup(importJsonInput)
                        if (success) {
                            Toast.makeText(context, "Settings restored successfully!", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                        } else {
                            Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight)
                ) {
                    Text("Apply Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Confirm Reset Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset to Factory Defaults?", color = Color.White) },
            text = {
                Text("This will restore default folders, wallpaper, dock apps, and layout settings.", color = Color(0xFFCBD5E1))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDefaults()
                        showResetConfirm = false
                        Toast.makeText(context, "Reset to defaults", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GalaxyCardDark)
            .border(1.dp, GalaxyCardBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, contentDescription = null, tint = OneUiCyanGlow, modifier = Modifier.size(20.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            content()
        }
    }
}

@Composable
fun GestureSettingItem(
    title: String,
    currentAction: GestureAction,
    onSelectAction: (GestureAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 13.sp)
            Text(currentAction.label, color = OneUiCyanGlow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GestureAction.values().forEach { action ->
                    Text(
                        text = "• ${action.label}",
                        color = if (action == currentAction) OneUiCyanGlow else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (action == currentAction) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectAction(action)
                                expanded = false
                            }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
