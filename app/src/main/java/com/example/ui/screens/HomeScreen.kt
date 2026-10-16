package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppItem
import com.example.data.SmartFolder
import com.example.ui.LauncherScreen
import com.example.ui.LauncherUiState
import com.example.ui.LauncherViewModel
import com.example.ui.components.OneUiAppIcon
import com.example.ui.components.OneUiCarousel3D
import com.example.ui.components.OneUiClockWeatherWidget
import com.example.ui.components.OneUiDock
import com.example.ui.components.OneUiFolderCard
import com.example.ui.components.OneUiSearchBar
import com.example.ui.components.OneUiWallpaper
import com.example.ui.components.OneUiWidgetsDashboard
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiCyanGlow
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

    // Parallax offset computation (-1f to 1f)
    val parallaxOffset by remember {
        derivedStateOf {
            if (uiState.settings.parallaxEnabled) {
                (pagerState.currentPage - 1) + pagerState.currentPageOffsetFraction
            } else 0f
        }
    }

    var verticalDragAccumulator by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.handleGesture(uiState.settings.doubleTapGesture, context)
                    },
                    onLongPress = {
                        viewModel.handleGesture(uiState.settings.longPressGesture, context)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        verticalDragAccumulator += dragAmount.y
                    },
                    onDragEnd = {
                        if (verticalDragAccumulator < -80f) {
                            viewModel.handleGesture(uiState.settings.swipeUpGesture, context)
                        } else if (verticalDragAccumulator > 80f) {
                            viewModel.handleGesture(uiState.settings.swipeDownGesture, context)
                        }
                        verticalDragAccumulator = 0f
                    },
                    onDragCancel = { verticalDragAccumulator = 0f }
                )
            }
            .testTag("home_screen_root")
    ) {
        // Wallpaper with Parallax
        OneUiWallpaper(
            wallpaper = uiState.settings.wallpaper,
            parallaxOffsetRatio = parallaxOffset
        )

        // Main Pager Pages
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("home_pager")
            ) { page ->
                when (page) {
                    0 -> PageMainHome(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    1 -> PageSmartOrganization(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    2 -> PageWidgetsDashboard(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }

            // Bottom Section: Page Indicators + Fixed Dock
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp).testTag("page_indicators")
                ) {
                    repeat(3) { idx ->
                        val isSelected = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) OneUiCyanGlow else Color(0x66CBD5E1)
                                )
                        )
                    }
                }

                // Signature OneUI Dock
                OneUiDock(
                    dockApps = uiState.dockApps,
                    onLaunchApp = { viewModel.launchApp(context, it) },
                    onAppLongClick = { viewModel.pinToDock(it) },
                    onOpenDrawer = { viewModel.setScreen(LauncherScreen.APP_DRAWER) }
                )
            }
        }
    }

    // Active Folder Popup Dialog
    uiState.activeFolder?.let { folder ->
        FolderDialog(
            folder = folder,
            allApps = uiState.installedApps,
            onDismiss = { viewModel.closeFolder() },
            onLaunchApp = { viewModel.launchApp(context, it) },
            onRenameFolder = { newName -> viewModel.renameFolder(folder.id, newName) },
            onAddAppToFolder = { pkg -> viewModel.addAppToFolder(folder.id, pkg) },
            onRemoveAppFromFolder = { pkg -> viewModel.removeAppFromFolder(folder.id, pkg) },
            onDeleteFolder = { viewModel.deleteFolder(folder.id) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageMainHome(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Clock & Editable Weather Widget
        OneUiClockWeatherWidget(
            weather = uiState.settings.weather,
            onUpdateWeather = { viewModel.updateWeather(it) }
        )

        // Google / Samsung style Search Pill
        OneUiSearchBar(
            onClickSearch = { viewModel.openSearch(context) },
            onClickVoice = { viewModel.openSearch(context) },
            onClickLens = { viewModel.openSearch(context) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Home Screen Featured Apps Grid
        val mainPageApps = remember(uiState.installedApps) {
            uiState.installedApps.take(8)
        }

        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = uiState.settings.gridSize.columns,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                mainPageApps.forEach { app ->
                    OneUiAppIcon(
                        app = app,
                        iconSize = uiState.settings.iconSize.sizeDp.dp,
                        showLabel = uiState.settings.showLabels,
                        onClick = { viewModel.launchApp(context, app) },
                        onLongClick = { viewModel.pinToDock(app) }
                    )
                }

                // Also showcase first 2 Smart Folders directly on home
                uiState.folders.take(2).forEach { folder ->
                    OneUiFolderCard(
                        folder = folder,
                        allApps = uiState.installedApps,
                        cardSize = uiState.settings.iconSize.sizeDp.dp,
                        showLabel = uiState.settings.showLabels,
                        onClick = { viewModel.openFolder(folder) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageSmartOrganization(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Rotating App Carousel Showcase
        OneUiCarousel3D(
            featuredApps = uiState.installedApps.take(7),
            onLaunchApp = { viewModel.launchApp(context, it) }
        )

        // Smart Folders Section (EverythingMe inspired)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = OneUiCyanGlow,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Smart Categorized Folders",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.folders.forEach { folder ->
                    OneUiFolderCard(
                        folder = folder,
                        allApps = uiState.installedApps,
                        cardSize = 64.dp,
                        showLabel = true,
                        onClick = { viewModel.openFolder(folder) }
                    )
                }
            }
        }

        // Quick Tools Bar: Settings & Lock Screen Preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(GalaxyCardDark)
                    .border(1.dp, GalaxyCardBorder, RoundedCornerShape(18.dp))
                    .clickable { viewModel.setScreen(LauncherScreen.SETTINGS) }
                    .padding(14.dp)
                    .testTag("quick_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = OneUiCyanGlow, modifier = Modifier.size(18.dp))
                    Text("Launcher Settings", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(GalaxyCardDark)
                    .border(1.dp, GalaxyCardBorder, RoundedCornerShape(18.dp))
                    .clickable { viewModel.setScreen(LauncherScreen.LOCK_PREVIEW) }
                    .padding(14.dp)
                    .testTag("quick_lock_preview_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    Text("AOD Lock Preview", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PageWidgetsDashboard(
    uiState: LauncherUiState,
    viewModel: LauncherViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        OneUiWidgetsDashboard(
            weather = uiState.settings.weather,
            onEditWeather = { viewModel.openSearch(viewModel.getApplication()) }
        )
    }
}
