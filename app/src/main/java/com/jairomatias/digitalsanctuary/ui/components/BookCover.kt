package com.jairomatias.digitalsanctuary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.jairomatias.digitalsanctuary.ui.theme.SepiaPaper

@Composable
fun BookCover(
    title: String,
    author: String,
    coverUrl: String = "",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (coverUrl.isNotEmpty()) {
            SubcomposeAsyncImage(
                model = coverUrl,
                contentDescription = "Cover for $title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = {
                    AcademicFallbackCover(title = title, author = author)
                },
                loading = {
                    AcademicFallbackCover(title = title, author = author)
                }
            )
        } else {
            AcademicFallbackCover(title = title, author = author)
        }
    }
}

@Composable
private fun AcademicFallbackCover(
    title: String,
    author: String
) {
    // Generate an individual premium scholarly fallback cover style programmatically
    val hash = title.hashCode()
    val bgColor = if (hash % 2 == 0) SepiaPaper else Color(0xFFF7F3F2)
    val hasBorder = hash % 3 == 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(12.dp)
            .drawBehind {
                clipRect {
                    // Let's draw standard minimal architectural margins or shapes to mimic physical publisher styles
                    val w = size.width
                    val h = size.height
                    
                    if (hasBorder) {
                        drawRect(
                            color = Color(0xFF76777B).copy(alpha = 0.2f),
                            topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(w - 8.dp.toPx(), h - 8.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    } else {
                        // Drawing diagonal structural geometric blocks matching "Design of Everyday things" or e-ink contrast
                        drawLine(
                            color = Color(0xFF76777B).copy(alpha = 0.15f),
                            start = androidx.compose.ui.geometry.Offset(0f, h * 0.75f),
                            end = androidx.compose.ui.geometry.Offset(w, h * 0.4f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                color = Color(0xFF0F0F0F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = author,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = Color(0xFF5F5E59),
                textAlign = TextAlign.Center
            )
        }
    }
}
