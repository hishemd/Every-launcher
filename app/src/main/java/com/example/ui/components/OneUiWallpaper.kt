package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.WallpaperOption
import kotlin.math.roundToInt

@Composable
fun OneUiWallpaper(
    wallpaper: WallpaperOption,
    parallaxOffsetRatio: Float = 0f,
    modifier: Modifier = Modifier
) {
    val parallaxPx = (parallaxOffsetRatio * -40f).roundToInt()

    Box(modifier = modifier.fillMaxSize()) {
        when (wallpaper) {
            WallpaperOption.GALAXY_NEBULA -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(parallaxPx, 0) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.galaxy_wallpaper),
                        contentDescription = "Samsung Galaxy Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Atmospheric subtle dark overlay to ensure widgets & app icons pop
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x33060A14),
                                        Color(0x1A091124),
                                        Color(0x66060914)
                                    )
                                )
                            )
                    )
                }
            }

            WallpaperOption.OBSIDIAN_DARK -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF030712),
                                    Color(0xFF0B1120),
                                    Color(0xFF020408)
                                )
                            )
                        )
                )
            }

            WallpaperOption.CYBER_CYAN -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0A2540),
                                    Color(0xFF071426),
                                    Color(0xFF030712)
                                ),
                                radius = 1200f
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.35f)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00E5FF),
                                        Color.Transparent,
                                        Color(0xFF2563EB)
                                    )
                                )
                            )
                    )
                }
            }

            WallpaperOption.AURORA_WAVE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E1035),
                                    Color(0xFF0F172A),
                                    Color(0xFF050816)
                                )
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.25f)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF8B5CF6),
                                        Color(0xFF06B6D4)
                                    )
                                )
                            )
                    )
                }
            }

            WallpaperOption.DEEP_SPACE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF020617),
                                    Color(0xFF0F172A),
                                    Color(0xFF000000)
                                )
                            )
                        )
                )
            }
        }
    }
}
