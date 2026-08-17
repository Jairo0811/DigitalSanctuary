package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    var currentTab by remember { mutableStateOf(NavigationTab.Library) }
    val books by viewModel.allBooks.collectAsState()
    val activeBookId by viewModel.selectedBookId.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    // Find the currently active book object
    val activeBook = books.find { it.id == activeBookId }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Context-Aware Top App Bar matching HTML exactly
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (currentTab) {
                                NavigationTab.Reading -> activeBook?.title ?: "The Architecture of Attention"
                                NavigationTab.Settings -> "E-ink Optimization"
                                else -> "Digital Sanctuary"
                            },
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF0F0F0F)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentTab == NavigationTab.Reading) {
                                currentTab = NavigationTab.Library
                            } else {
                                // Default drawer trigger
                            }
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("scaffold_nav_icon")
                    ) {
                        Icon(
                            imageVector = when (currentTab) {
                                NavigationTab.Reading -> Icons.Default.ArrowBack
                                else -> Icons.Default.MenuBook
                            },
                            contentDescription = "Left menu navigation action",
                            tint = Color(0xFF0F0F0F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (currentTab == NavigationTab.Reading) {
                                // Toggle bookmark or settings shortcut
                            } else {
                                currentTab = NavigationTab.Settings
                            }
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = when (currentTab) {
                                NavigationTab.Reading -> Icons.Default.BookmarkBorder
                                else -> Icons.Default.SettingsInputComponent
                            },
                            contentDescription = "Right setting action",
                            tint = Color(0xFF0F0F0F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.85f),
                    titleContentColor = Color(0xFF0F0F0F)
                ),
                modifier = Modifier.border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            )
        },
        bottomBar = {
            // Adaptive, Polish Bottom Navigation Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .sizeIn(minHeight = 64.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        NavigationTabInfo(NavigationTab.Library, "Library", Icons.Outlined.AutoStories, Icons.Filled.AutoStories, "tab_library"),
                        NavigationTabInfo(NavigationTab.Reading, "Reading", Icons.Outlined.MenuBook, Icons.Filled.MenuBook, "tab_reading"),
                        NavigationTabInfo(NavigationTab.Notes, "Notes", Icons.Outlined.AutoAwesomeMotion, Icons.Filled.AutoAwesomeMotion, "tab_notes"),
                        NavigationTabInfo(NavigationTab.Settings, "Settings", Icons.Outlined.Tune, Icons.Filled.Tune, "tab_settings")
                    )

                    tabs.forEach { tabInfo ->
                        val isSelected = currentTab == tabInfo.tab

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clickable { currentTab = tabInfo.tab }
                                .padding(8.dp)
                                .testTag(tabInfo.testTag)
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF0F0F0F), CircleShape)
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = tabInfo.filledIcon,
                                        contentDescription = tabInfo.label,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tabInfo.outlinedIcon,
                                    contentDescription = tabInfo.label,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = 54.dp)
        ) {
            // Dynamic Screen Transitions
            when (currentTab) {
                NavigationTab.Library -> {
                    LibraryScreen(
                        viewModel = viewModel,
                        onNavigateToReader = {
                            currentTab = NavigationTab.Reading
                        }
                    )
                }
                NavigationTab.Reading -> {
                    ReaderScreen(viewModel = viewModel)
                }
                NavigationTab.Notes -> {
                    NotesScreen(viewModel = viewModel)
                }
                NavigationTab.Settings -> {
                    SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

private enum class NavigationTab {
    Library, Reading, Notes, Settings
}

private data class NavigationTabInfo(
    val tab: NavigationTab,
    val label: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector,
    val testTag: String
)
