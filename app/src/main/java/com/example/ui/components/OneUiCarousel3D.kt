package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppItem
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiBlueLight
import com.example.ui.theme.OneUiCyanGlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun OneUiCarousel3D(
    featuredApps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredApps.isEmpty()) return

    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    val dragOffset = remember { Animatable(0f) }

    val safeApps = remember(featuredApps) {
        if (featuredApps.size < 3) {
            // Repeat to form a full ring
            featuredApps + featuredApps + featuredApps
        } else featuredApps
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("one_ui_carousel_3d"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section header with glowing 3D badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = OneUiCyanGlow,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "3D Quick Carousel",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Carousel Navigation Controls
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = {
                        scope.launch {
                            currentIndex = (currentIndex - 1 + safeApps.size) % safeApps.size
                        }
                    },
                    modifier = Modifier.size(32.dp).testTag("carousel_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous app",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            currentIndex = (currentIndex + 1) % safeApps.size
                        }
                    },
                    modifier = Modifier.size(32.dp).testTag("carousel_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next app",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 3D Perspective Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val offset = dragOffset.value
                                if (offset > 40f) {
                                    currentIndex = (currentIndex - 1 + safeApps.size) % safeApps.size
                                } else if (offset < -40f) {
                                    currentIndex = (currentIndex + 1) % safeApps.size
                                }
                                dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                dragOffset.snapTo(dragOffset.value + dragAmount * 0.4f)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Render relative items: -2, -1, 0, 1, 2
            for (relOffset in -2..2) {
                val itemIndex = (currentIndex + relOffset + safeApps.size * 10) % safeApps.size
                val app = safeApps[itemIndex]
                val itemDragFrac = (dragOffset.value / 250f).coerceIn(-1f, 1f)
                val visualOffset = relOffset - itemDragFrac

                val rotY = (visualOffset * 40f).coerceIn(-75f, 75f)
                val scale = (1f - abs(visualOffset) * 0.24f).coerceIn(0.55f, 1f)
                val transX = visualOffset * 115f
                val alpha = (1f - abs(visualOffset) * 0.35f).coerceIn(0.2f, 1f)

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = transX * density
                            rotationY = rotY
                            cameraDistance = 18f * density
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .shadow(
                            elevation = if (relOffset == 0) 16.dp else 4.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = if (relOffset == 0) OneUiCyanGlow else Color.Transparent
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(GalaxyCardDark)
                        .border(
                            width = if (relOffset == 0) 1.5.dp else 1.dp,
                            brush = if (relOffset == 0) {
                                Brush.verticalGradient(listOf(OneUiCyanGlow, GalaxyCardBorder))
                            } else {
                                Brush.verticalGradient(listOf(GalaxyCardBorder, Color.Transparent))
                            },
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(14.dp)
                        .width(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OneUiAppIcon(
                            app = app,
                            iconSize = 54.dp,
                            showLabel = false,
                            onClick = { onLaunchApp(app) }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = app.label,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = app.category.title,
                            color = OneUiCyanGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        if (relOffset == 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onLaunchApp(app) },
                                colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp).testTag("carousel_launch_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Launch",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Pager dots for carousel
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            safeApps.take(7).forEachIndexed { idx, _ ->
                val isSelected = (currentIndex % 7) == idx
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) OneUiCyanGlow else Color(0x6694A3B8))
                )
            }
        }
    }
}
