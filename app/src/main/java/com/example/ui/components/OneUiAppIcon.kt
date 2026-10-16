package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.IconBrowserPurple
import com.example.ui.theme.IconCalendarTeal
import com.example.ui.theme.IconCameraRed
import com.example.ui.theme.IconClockAmber
import com.example.ui.theme.IconGalleryOrange
import com.example.ui.theme.IconMessageBlue
import com.example.ui.theme.IconMusicPink
import com.example.ui.theme.IconPhoneGreen
import com.example.ui.theme.IconSettingsSlate
import com.example.ui.theme.IconWeatherYellow

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OneUiAppIcon(
    app: AppItem,
    iconSize: Dp = 56.dp,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "iconPressScale"
    )

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
            .testTag("app_icon_${app.packageName}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val cornerRadius = iconSize * 0.32f
        val squircleShape = RoundedCornerShape(cornerRadius)

        Box(
            modifier = Modifier
                .size(iconSize)
                .shadow(
                    elevation = if (isPressed) 2.dp else 8.dp,
                    shape = squircleShape,
                    ambientColor = Color(0x3300D2FF),
                    spotColor = Color(0x660044FF)
                )
                .clip(squircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (app.iconBitmap != null) {
                Image(
                    bitmap = app.iconBitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                // Authentic OneUI styled fallback squircle icon
                val (gradient, vectorIcon) = getThemedIconData(app)
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(brush = gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = vectorIcon,
                        contentDescription = app.label,
                        tint = Color.White,
                        modifier = Modifier.size(iconSize * 0.52f)
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("app_label_${app.packageName}")
            )
        }
    }
}

private fun getThemedIconData(app: AppItem): Pair<Brush, ImageVector> {
    val name = "${app.label.lowercase()} ${app.packageName.lowercase()}"
    return when {
        name.contains("phone") || name.contains("dialer") ->
            Brush.verticalGradient(listOf(IconPhoneGreen, Color(0xFF059669))) to Icons.Default.Phone
        name.contains("message") || name.contains("chat") ->
            Brush.verticalGradient(listOf(Color(0xFF38BDF8), IconMessageBlue)) to Icons.Default.Send
        name.contains("camera") ->
            Brush.verticalGradient(listOf(Color(0xFFF43F5E), IconCameraRed)) to Icons.Default.PhotoCamera
        name.contains("gallery") || name.contains("photo") ->
            Brush.verticalGradient(listOf(Color(0xFFFB923C), IconGalleryOrange)) to Icons.Default.Image
        name.contains("setting") ->
            Brush.verticalGradient(listOf(Color(0xFF64748B), IconSettingsSlate)) to Icons.Default.Settings
        name.contains("browser") || name.contains("internet") || name.contains("chrome") ->
            Brush.verticalGradient(listOf(Color(0xFF818CF8), IconBrowserPurple)) to Icons.Default.Language
        name.contains("clock") ->
            Brush.verticalGradient(listOf(Color(0xFFFBBF24), IconClockAmber)) to Icons.Default.Schedule
        name.contains("calendar") ->
            Brush.verticalGradient(listOf(Color(0xFF2DD4BF), IconCalendarTeal)) to Icons.Default.CalendarToday
        name.contains("calculator") ->
            Brush.verticalGradient(listOf(Color(0xFF60A5FA), Color(0xFF2563EB))) to Icons.Default.Calculate
        name.contains("note") ->
            Brush.verticalGradient(listOf(Color(0xFFFCD34D), Color(0xFFD97706))) to Icons.Default.EditNote
        name.contains("weather") ->
            Brush.verticalGradient(listOf(Color(0xFFFDE047), IconWeatherYellow)) to Icons.Default.WbSunny
        name.contains("music") || name.contains("audio") || name.contains("spotify") ->
            Brush.verticalGradient(listOf(Color(0xFFF472B6), IconMusicPink)) to Icons.Default.MusicNote
        else ->
            Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))) to Icons.Default.Widgets
    }
}
