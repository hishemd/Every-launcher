package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AppItem
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiCyanGlow

@Composable
fun OneUiDock(
    dockApps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = Color.Black, spotColor = Color(0x6600E5FF))
            .clip(RoundedCornerShape(32.dp))
            .background(GalaxyCardDark)
            .border(1.dp, GalaxyCardBorder, RoundedCornerShape(32.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("one_ui_dock"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.take(4).forEach { app ->
                OneUiAppIcon(
                    app = app,
                    iconSize = 52.dp,
                    showLabel = false,
                    onClick = { onLaunchApp(app) },
                    onLongClick = { onAppLongClick(app) }
                )
            }

            // App Drawer Button (Signature 9-dot / apps grid icon)
            val drawerFakeApp = AppItem(
                packageName = "launcher.internal.drawer",
                activityName = "",
                label = "Apps"
            )
            OneUiAppIcon(
                app = drawerFakeApp,
                iconSize = 52.dp,
                showLabel = false,
                onClick = onOpenDrawer
            )
        }
    }
}
