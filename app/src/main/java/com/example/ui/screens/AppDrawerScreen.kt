package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppCategory
import com.example.data.AppItem
import com.example.data.LauncherSettings
import com.example.ui.components.OneUiAppIcon
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiBlueLight
import com.example.ui.theme.OneUiCyanGlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(
    allApps: List<AppItem>,
    recentApps: List<AppItem>,
    settings: LauncherSettings,
    onLaunchApp: (AppItem) -> Unit,
    onPinToDock: (AppItem) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
    var selectedAppForSheet by remember { mutableStateOf<AppItem?>(null) }

    BackHandler {
        onCloseDrawer()
    }

    // Filter apps based on category and search query
    val filteredApps = remember(allApps, searchQuery, selectedCategory) {
        allApps.filter { app ->
            val matchesCategory = (selectedCategory == AppCategory.ALL) || (app.category == selectedCategory)
            val matchesQuery = searchQuery.isBlank() ||
                    app.label.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    // Alphabetical jump letters
    val alphabet = ('A'..'Z').toList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xF0060914),
                        Color(0xF50C1428),
                        Color(0xFA060A14)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("app_drawer_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("drawer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to home",
                        tint = Color.White
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search ${allApps.size} apps...", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OneUiCyanGlow)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OneUiCyanGlow,
                        unfocusedBorderColor = GalaxyCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = GalaxyCardDark,
                        unfocusedContainerColor = GalaxyCardDark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).testTag("drawer_search_input")
                )
            }

            // Category Chips Row
            ScrollableTabRow(
                selectedTabIndex = AppCategory.values().indexOf(selectedCategory),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                divider = {},
                indicator = {}
            ) {
                AppCategory.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) OneUiBlueLight else Color(0xFF1E293B))
                            .border(
                                1.dp,
                                if (isSelected) OneUiCyanGlow else Color(0xFF334155),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Recent Apps Section (shown when no search query is active)
            if (searchQuery.isBlank() && recentApps.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                    Text(
                        text = "Recent Apps",
                        color = OneUiCyanGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
                    ) {
                        items(recentApps) { app ->
                            OneUiAppIcon(
                                app = app,
                                iconSize = 48.dp,
                                showLabel = settings.showLabels,
                                onClick = { onLaunchApp(app) },
                                onLongClick = { selectedAppForSheet = app }
                            )
                        }
                    }
                }
            }

            // Apps Grid & Alphabetical Index Bar
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.gridSize.columns),
                    state = gridState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .testTag("drawer_apps_grid"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        OneUiAppIcon(
                            app = app,
                            iconSize = settings.iconSize.sizeDp.dp,
                            showLabel = settings.showLabels,
                            onClick = { onLaunchApp(app) },
                            onLongClick = { selectedAppForSheet = app }
                        )
                    }
                }

                // Alphabetical Quick Jump Navigation
                Column(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    alphabet.forEach { letter ->
                        Text(
                            text = letter.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier
                                .clickable {
                                    val targetIndex = filteredApps.indexOfFirst {
                                        it.label.startsWith(letter, ignoreCase = true)
                                    }
                                    if (targetIndex >= 0) {
                                        scope.launch {
                                            gridState.animateScrollToItem(targetIndex)
                                        }
                                    }
                                }
                                .padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }

    // App Options Bottom Sheet on Long Press
    selectedAppForSheet?.let { app ->
        ModalBottomSheet(
            onDismissRequest = { selectedAppForSheet = null },
            containerColor = Color(0xFF0F172A),
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // App header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OneUiAppIcon(
                        app = app,
                        iconSize = 48.dp,
                        showLabel = false,
                        onClick = {}
                    )
                    Column {
                        Text(app.label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(app.packageName, fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF334155))
                )

                // Option 1: Pin to Dock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onPinToDock(app)
                            selectedAppForSheet = null
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = null, tint = OneUiCyanGlow)
                    Text("Pin to Favorites Dock", color = Color.White, fontSize = 15.sp)
                }

                // Option 2: App Info (Settings)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", app.packageName, null)
                            }
                            try { context.startActivity(intent) } catch (_: Exception) {}
                            selectedAppForSheet = null
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8))
                    Text("App Info", color = Color.White, fontSize = 15.sp)
                }

                // Option 3: Uninstall (if not system)
                if (!app.isSystemApp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_DELETE).apply {
                                    data = Uri.fromParts("package", app.packageName, null)
                                }
                                try { context.startActivity(intent) } catch (_: Exception) {}
                                selectedAppForSheet = null
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF43F5E))
                        Text("Uninstall App", color = Color(0xFFF43F5E), fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
