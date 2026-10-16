package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeatherConfig
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardBorderGlow
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiBlueLight
import com.example.ui.theme.OneUiCyanGlow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OneUiClockWeatherWidget(
    weather: WeatherConfig,
    onUpdateWeather: (WeatherConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMMM d", Locale.getDefault())

    val timeStr = timeFormat.format(currentTime)
    val dateStr = dateFormat.format(currentTime)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("clock_weather_widget"),
        horizontalAlignment = Alignment.Start
    ) {
        // OneUI signature digital clock
        Text(
            text = timeStr,
            fontSize = 62.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp,
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.testTag("clock_display")
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = dateStr,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.testTag("date_display")
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(Color(0xFF64748B), CircleShape)
            )

            // Weather pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GalaxyCardDark)
                    .border(1.dp, GalaxyCardBorder, RoundedCornerShape(16.dp))
                    .clickable { showEditDialog = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("weather_pill_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = getWeatherIcon(weather.condition),
                        contentDescription = "Weather Icon",
                        tint = OneUiCyanGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${weather.temperature}${weather.unit} ${weather.condition}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    // Labeled demo badge
                    if (weather.isDemo) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3300E5FF))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "DEMO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = OneUiCyanGlow
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit weather",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        WeatherEditDialog(
            currentConfig = weather,
            onDismiss = { showEditDialog = false },
            onSave = {
                onUpdateWeather(it)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun WeatherEditDialog(
    currentConfig: WeatherConfig,
    onDismiss: () -> Unit,
    onSave: (WeatherConfig) -> Unit
) {
    var city by remember { mutableStateOf(currentConfig.city) }
    var tempStr by remember { mutableStateOf(currentConfig.temperature.toString()) }
    var condition by remember { mutableStateOf(currentConfig.condition) }
    var unit by remember { mutableStateOf(currentConfig.unit) }

    val conditions = listOf("Sunny", "Partly Cloudy", "Rain", "Thunderstorm", "Snow")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Customize Weather Widget (Demo Mode)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Adjust local weather conditions or city for launcher preview display.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City / Region") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OneUiCyanGlow,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("weather_city_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tempStr,
                        onValueChange = { tempStr = it.filter { char -> char.isDigit() || char == '-' } },
                        label = { Text("Temperature") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OneUiCyanGlow,
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("weather_temp_input")
                    )

                    Button(
                        onClick = { unit = if (unit == "°C") "°F" else "°C" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(unit, color = OneUiCyanGlow, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Condition:", fontSize = 13.sp, color = Color(0xFFCBD5E1))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    conditions.take(3).forEach { cond ->
                        val isSelected = condition == cond
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) OneUiBlueLight else Color(0xFF1E293B))
                                .clickable { condition = cond }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cond,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedTemp = tempStr.toIntOrNull() ?: 22
                    onSave(
                        currentConfig.copy(
                            city = city.ifBlank { "Seoul, KR" },
                            temperature = parsedTemp,
                            condition = condition,
                            unit = unit,
                            isDemo = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight)
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(24.dp)
    )
}

private fun getWeatherIcon(condition: String) = when (condition.lowercase()) {
    "sunny", "clear" -> Icons.Default.WbSunny
    "partly cloudy" -> Icons.Default.WbCloudy
    "rain", "shower" -> Icons.Default.Grain
    "thunderstorm" -> Icons.Default.Thunderstorm
    "snow" -> Icons.Default.AcUnit
    else -> Icons.Default.Cloud
}
