package com.jairomatias.digitalsanctuary.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jairomatias.digitalsanctuary.ui.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section: Info Header banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ergonomics_info_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Readability eye",
                        tint = Color(0xFF0F0F0F),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Digital Ergonomics",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F0F0F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "These settings optimize UI rendering for E-ink hardware. Adjustments here reduce ghosting, minimize unnecessary refreshes, and cap animation durations to 100ms for a frictionless reading experience.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Section: Settings list panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Item 1: HD Symbol Logic
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "HD Symbol Logic",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F0F0F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Forces solid, high-contrast iconography instead of variable weight outlines to improve legibility on low-refresh displays.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Switch(
                        checked = settings.hdSymbolLogic,
                        onCheckedChange = { viewModel.toggleHdSymbolLogic() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0F0F0F)
                        ),
                        modifier = Modifier.testTag("hd_symbol_switch")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 2: Animation Duration Range slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Animation Filter (Duration)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F0F0F)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Caps UI transition and animation durations.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${settings.animationDuration}ms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F0F0F),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Slider(
                        value = settings.animationDuration.toFloat(),
                        onValueChange = { viewModel.updateAnimationDuration(it.toInt()) },
                        valueRange = 0f..300f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0F0F0F),
                            activeTrackColor = Color(0xFF0F0F0F),
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("animation_filter_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0ms", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = Color(0xFF76777B))
                        Text("100ms", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = Color(0xFF76777B))
                        Text("300ms", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = Color(0xFF76777B))
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 3: UI Bleach Level Option Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = "UI Bleach Level",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F0F0F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Increases contrast of backgrounds to stark white to reduce tonal muddiness on E-ink.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Standard", "High", "Max (1-bit)").forEach { level ->
                            val levelValue = level.split(" ")[0] // Map Max (1-bit) back to Max
                            val isSelected = settings.bleachLevel == levelValue

                            Button(
                                onClick = { viewModel.updateBleachLevel(levelValue) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bleach_button_$levelValue"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color.Black else Color.Transparent)
                            ) {
                                Text(
                                    text = level,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Hardware Refresh Mode
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "HARDWARE REFRESH MODE",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF76777B),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val refreshOptions = listOf(
                        RefreshModeItem("Normal", "Normal (Quality)", "Full screen refresh. Best for reading static text. Highest quality, slowest response.", Icons.Default.Book),
                        RefreshModeItem("A2", "A2 Mode", "Faster partial refresh. Minimal ghosting. Recommended for UI navigation and scrolling.", Icons.Default.SwipeUp),
                        RefreshModeItem("Speed", "Speed Mode", "Prioritizes responsiveness over image quality. Highest ghosting. Best for typing.", Icons.Default.Bolt)
                    )

                    refreshOptions.forEachIndexed { idx, option ->
                        val isSelected = settings.refreshMode == option.key

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = { viewModel.updateRefreshMode(option.key) }
                                )
                                .padding(16.dp)
                                .testTag("refresh_option_${option.key}"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateRefreshMode(option.key) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF0F0F0F)
                                )
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F0F0F)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = option.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.title,
                                tint = Color(0xFF76777B),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (idx < refreshOptions.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

private data class RefreshModeItem(
    val key: String,
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
