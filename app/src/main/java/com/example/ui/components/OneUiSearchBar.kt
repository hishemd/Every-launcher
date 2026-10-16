package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiCyanGlow

@Composable
fun OneUiSearchBar(
    onClickSearch: () -> Unit,
    onClickVoice: () -> Unit,
    onClickLens: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(10.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x33000000), spotColor = Color(0x4000D2FF))
            .clip(RoundedCornerShape(28.dp))
            .background(GalaxyCardDark)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(GalaxyCardBorder, Color(0x6600E5FF), GalaxyCardBorder)
                ),
                RoundedCornerShape(28.dp)
            )
            .clickable { onClickSearch() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("one_ui_search_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vibrant Search Icon / "G" style emblem
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2563EB), Color(0xFF00E5FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Search apps or web...",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = OneUiCyanGlow,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClickVoice() }
                        .testTag("voice_search_button")
                )

                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = "Smart Lens",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClickLens() }
                        .testTag("lens_search_button")
                )
            }
        }
    }
}
