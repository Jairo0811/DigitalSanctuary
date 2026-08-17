package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.SepiaPaper

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settingsState.collectAsState()
    val scrollState = rememberScrollState()

    var showNavBallMenu by remember { mutableStateOf(false) }
    var highlightedConcept by remember { mutableStateOf<String?>(null) }

    // Synchronize scroll depth with current book reading progress
    LaunchedEffect(scrollState.value) {
        val maxScroll = scrollState.maxValue
        if (maxScroll > 0) {
            val progress = scrollState.value.toFloat() / maxScroll
            viewModel.updateBookProgress("architecture_attention", progress.coerceIn(0f, 1f))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (settings.bleachLevel == "Standard") SepiaPaper else MaterialTheme.colorScheme.background
            )
    ) {
        // Core Reader Canvas Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp)
        ) {
            // Book Cover Hero Banner / Scholarly Spacing
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Large styled chapter headline
                Text(
                    text = "The Architecture of Attention in the Digital Age",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (settings.textSize + 8).sp,
                    lineHeight = (settings.textSize + 14).sp,
                    color = Color(0xFF0F0F0F),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Chapter 3: Cognitive Friction",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Time stamp & Jenga mode row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            // Thin separators
                            val yTop = 0f
                            val yBottom = size.height
                            drawLine(
                                color = Color(0xFFC7C6CA),
                                start = androidx.compose.ui.geometry.Offset(0f, yTop),
                                end = androidx.compose.ui.geometry.Offset(size.width, yTop),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFFC7C6CA),
                                start = androidx.compose.ui.geometry.Offset(0f, yBottom),
                                end = androidx.compose.ui.geometry.Offset(size.width, yBottom),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Reading Time: 15 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Jenga Mode: ${if (settings.jengaMode) "Active" else "Inactive"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Body content layout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Jenga blocks paragraph 1
                JengaParagraph(
                    lines = listOf(
                        "The modern interface is designed not for utility,",
                        "but for extraction.",
                        "Every smooth transition,",
                        "every frictionless interaction,",
                        "is a carefully calculated mechanism",
                        "intended to bypass cognitive resistance."
                    ),
                    isActive = settings.jengaMode,
                    fontSize = settings.textSize
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive paragraph connecting highlight to Sidenotes
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (settings.jengaMode) {
                        JengaSentenceBlock(
                            text = "When we consider the principles of ",
                            boldAccentText = "Digital Ergonomics",
                            suffixText = ", we must evaluate the true cost of 'easy'.",
                            fontSize = settings.textSize,
                            onAccentClick = {
                                highlightedConcept = if (highlightedConcept == "ergonomics") null else "ergonomics"
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        JengaParagraph(
                            lines = listOf(
                                "If an action requires no thought,",
                                "it also commands no attention.",
                                "This leads to the paradox of modern design:",
                                "Interfaces that demand our time,",
                                "yet reject our focus."
                            ),
                            isActive = true,
                            fontSize = settings.textSize
                        )
                    } else {
                        // Regular flow paragraph
                        Text(
                            text = "When we consider the principles of Digital Ergonomics, we must evaluate the true cost of 'easy'. If an action requires no thought, it also commands no attention. This leads to the paradox of modern design: Interfaces that demand our time, yet reject our focus.",
                            fontFamily = FontFamily.Serif,
                            fontSize = settings.textSize.sp,
                            lineHeight = (settings.textSize + 10).sp,
                            color = Color(0xFF0F0F0F)
                        )
                    }
                }

                // Interactive Inline Sidenote panel for Mobile devices
                AnimatedVisibility(
                    visible = highlightedConcept == "ergonomics",
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("inline_sidenote_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        border = BorderStroke(1.dp, Color(0xFF0F0F0F))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CONCEPT DEFINITION",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF76777B)
                                )
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info Icon",
                                    tint = Color(0xFF76777B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Digital Ergonomics: The practice of designing software interfaces that respect human cognitive limits and prioritize mental wellbeing over engagement metrics.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                JengaParagraph(
                    lines = listOf(
                        "To reclaim focus,",
                        "we must intentionally reintroduce friction.",
                        "Not arbitrary difficulty,",
                        "but meaningful structural pauses.",
                        "Visual-Syntactic Text Formatting",
                        "acts as one such friction point,",
                        "forcing the eye to acknowledge semantic boundaries."
                    ),
                    isActive = settings.jengaMode,
                    fontSize = settings.textSize
                )

                // High Contrast Quote block
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "\"The medium is no longer just the message; the interface is the governor of our cognitive bandwidth.\"",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = (settings.textSize + 2).sp,
                            lineHeight = (settings.textSize + 8).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "— DR. ELIAS VANCE, MEDIA THEORIST",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color(0xFF76777B)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                JengaParagraph(
                    lines = listOf(
                        "Consider the E-ink display.",
                        "Its physical limitations—",
                        "the slow refresh rate, the lack of vivid color—",
                        "serve as a natural barrier to mindless scrolling.",
                        "It is a sanctuary built of technical constraints."
                    ),
                    isActive = settings.jengaMode,
                    fontSize = settings.textSize
                )
            }
        }

        // Reading progress background horizontal indicator strip
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            val progressFactor = if (scrollState.maxValue > 0) {
                scrollState.value.toFloat() / scrollState.maxValue
            } else 0f

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressFactorValue(progressFactor))
                    .background(Color(0xFF0F0F0F))
            )
        }

        // Draggable floating Settings "Nav Ball"
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .zIndex(99f)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expanding options panel
                AnimatedVisibility(
                    visible = showNavBallMenu,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .testTag("nav_ball_quick_settings"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        border = BorderStroke(2.dp, Color(0xFF0F0F0F)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Display Settings",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F0F0F)
                                )
                                IconButton(
                                    onClick = { showNavBallMenu = false },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Options",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Jenga Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Jenga Format",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F0F0F)
                                    )
                                    Text(
                                        text = "Syntactic blocks",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = Color(0xFF76777B)
                                    )
                                }

                                Switch(
                                    checked = settings.jengaMode,
                                    onCheckedChange = { viewModel.toggleJengaMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF0F0F0F)
                                    ),
                                    modifier = Modifier.testTag("jenga_format_toggle")
                                )
                            }

                            // Text Size Adjuster
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Text Size",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F0F0F)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateTextSize(false) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease Font Size",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Text(
                                        text = settings.textSize.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(20.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    IconButton(
                                        onClick = { viewModel.updateTextSize(true) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase Font Size",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Bleach / Contrast Level
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Column {
                                    Text(
                                        text = "Bleach Level",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F0F0F)
                                    )
                                    Text(
                                        text = "Contrast Mode",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = Color(0xFF76777B)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Standard", "High", "Max").forEach { level ->
                                        val isSelected = settings.bleachLevel == level
                                        Button(
                                            onClick = { viewModel.updateBleachLevel(level) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(vertical = 4.dp)
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
                }

                // Nav Ball Toggle Action button matching HTML "tune" button exactly!
                FloatingActionButton(
                    onClick = { showNavBallMenu = !showNavBallMenu },
                    containerColor = Color(0xFF0F0F0F), // Ink Black
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("nav_ball_toggle")
                ) {
                    Icon(
                        imageVector = if (showNavBallMenu) Icons.Default.Close else Icons.Default.Tune,
                        contentDescription = "Floating display options",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Custom Syntactic formatting blocks for Jenga visual flow
@Composable
fun JengaParagraph(
    lines: List<String>,
    isActive: Boolean,
    fontSize: Int
) {
    if (isActive) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lines.forEach { line ->
                var isSelected by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isSelected = !isSelected
                        }
                        .drawBehind {
                            // Left tactical marker bar
                            val strokeWidthVal = 2.dp.toPx()
                            val lineColor = if (isSelected) Color(0xFF0F0F0F) else Color(0xFFC7C6CA)
                            drawLine(
                                color = lineColor,
                                start = androidx.compose.ui.geometry.Offset(strokeWidthVal / 2, 0f),
                                end = androidx.compose.ui.geometry.Offset(strokeWidthVal / 2, size.height),
                                strokeWidth = strokeWidthVal
                            )
                        }
                        .padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
                ) {
                    Text(
                        text = line,
                        fontFamily = FontFamily.Serif,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize + 6).sp,
                        color = if (isSelected) Color(0xFF0F0F0F) else Color(0xFF46474A),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    } else {
        // Flat normal paragraph rendering
        Text(
            text = lines.joinToString(" "),
            fontFamily = FontFamily.Serif,
            fontSize = fontSize.sp,
            lineHeight = (fontSize + 8).sp,
            color = Color(0xFF0F0F0F),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Special highlighted inline action line constructor
@Composable
fun JengaSentenceBlock(
    text: String,
    boldAccentText: String,
    suffixText: String,
    fontSize: Int,
    onAccentClick: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isSelected = !isSelected
            }
            .drawBehind {
                val strokeWidthVal = 2.dp.toPx()
                val lineColor = if (isSelected) Color(0xFF0F0F0F) else Color(0xFFC7C6CA)
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(strokeWidthVal / 2, 0f),
                    end = androidx.compose.ui.geometry.Offset(strokeWidthVal / 2, size.height),
                    strokeWidth = strokeWidthVal
                )
            }
            .padding(start = 16.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = text,
                    fontFamily = FontFamily.Serif,
                    fontSize = fontSize.sp,
                    color = if (isSelected) Color(0xFF0F0F0F) else Color(0xFF46474A)
                )

                Surface(
                    color = Color(0xFFFFFDE7).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clickable { onAccentClick() }
                        .padding(horizontal = 2.dp)
                        .testTag("sidenote_trigger_text")
                ) {
                    Text(
                        text = boldAccentText,
                        fontFamily = FontFamily.Serif,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F0F0F),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = suffixText,
                    fontFamily = FontFamily.Serif,
                    fontSize = fontSize.sp,
                    color = if (isSelected) Color(0xFF0F0F0F) else Color(0xFF46474A)
                )
            }
        }
    }
}

// Safeguard function mapping float bounding parameters
private fun progressFactorValue(input: Float): Float {
    return input.coerceIn(0.01f, 1.0f)
}
