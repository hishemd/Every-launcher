package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppItem
import com.example.data.SmartFolder
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiCyanGlow

@OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OneUiFolderCard(
    folder: SmartFolder,
    allApps: List<AppItem>,
    cardSize: Dp = 68.dp,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "folderPressScale"
    )

    val folderApps = remember(folder.appPackageNames, allApps) {
        allApps.filter { it.packageName in folder.appPackageNames }
    }

    Column(
        modifier = modifier
            .scale(scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick?.invoke()
                }
            )
            .testTag("folder_card_${folder.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val cornerRadius = cardSize * 0.32f
        val squircleShape = RoundedCornerShape(cornerRadius)

        Box(
            modifier = Modifier
                .size(cardSize)
                .shadow(
                    elevation = if (isPressed) 2.dp else 6.dp,
                    shape = squircleShape,
                    ambientColor = Color.Black,
                    spotColor = Color(0x6600E5FF)
                )
                .clip(squircleShape)
                .background(GalaxyCardDark)
                .border(1.dp, GalaxyCardBorder, squircleShape)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // 2x2 miniature icon grid
            FlowRow(
                modifier = Modifier.size(cardSize - 12.dp),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                folderApps.take(4).forEach { app ->
                    Box(modifier = Modifier.size((cardSize - 20.dp) / 2)) {
                        OneUiAppIcon(
                            app = app,
                            iconSize = (cardSize - 20.dp) / 2,
                            showLabel = false,
                            onClick = onClick
                        )
                    }
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = folder.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
