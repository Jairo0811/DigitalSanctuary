package com.jairomatias.digitalsanctuary.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jairomatias.digitalsanctuary.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(NavigationTab.Library) }
    val books by viewModel.allBooks.collectAsState()
    val activeBookId by viewModel.selectedBookId.collectAsState()
    val importMessage by viewModel.importMessage.collectAsState()
    val activeBook = books.find { it.id == activeBookId }
    val snackbarHostState = remember { SnackbarHostState() }

    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(viewModel::importDocument)
    }

    LaunchedEffect(importMessage) {
        importMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearImportMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            NavigationTab.Reading -> activeBook?.title ?: "Reader"
                            NavigationTab.Notes -> "Knowledge Hub"
                            NavigationTab.Settings -> "Reading Settings"
                            NavigationTab.Library -> "Digital Sanctuary"
                        },
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { if (currentTab == NavigationTab.Reading) currentTab = NavigationTab.Library },
                        modifier = Modifier.testTag("scaffold_nav_icon")
                    ) {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.Reading) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = if (currentTab == NavigationTab.Reading) "Back to library" else "Digital Sanctuary"
                        )
                    }
                },
                actions = {
                    when (currentTab) {
                        NavigationTab.Library -> IconButton(
                            onClick = { documentPicker.launch(arrayOf("application/epub+zip", "application/pdf")) },
                            modifier = Modifier.testTag("import_document")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Import EPUB or PDF")
                        }
                        NavigationTab.Reading -> Unit
                        else -> IconButton(onClick = { currentTab = NavigationTab.Settings }) {
                            Icon(Icons.Default.SettingsInputComponent, contentDescription = "Reading settings")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
                modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .sizeIn(minHeight = 64.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        NavigationTabInfo(NavigationTab.Library, "Library", Icons.Outlined.AutoStories, Icons.Filled.AutoStories, "tab_library"),
                        NavigationTabInfo(NavigationTab.Reading, "Reading", Icons.AutoMirrored.Outlined.MenuBook, Icons.AutoMirrored.Filled.MenuBook, "tab_reading"),
                        NavigationTabInfo(NavigationTab.Notes, "Knowledge", Icons.Outlined.AutoAwesomeMotion, Icons.Filled.AutoAwesomeMotion, "tab_notes"),
                        NavigationTabInfo(NavigationTab.Settings, "Settings", Icons.Outlined.Tune, Icons.Filled.Tune, "tab_settings")
                    )
                    tabs.forEach { tab -> BottomTab(tab, currentTab == tab.tab) { currentTab = tab.tab } }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentTab) {
                NavigationTab.Library -> LibraryScreen(viewModel = viewModel, onNavigateToReader = { currentTab = NavigationTab.Reading })
                NavigationTab.Reading -> ReaderScreen(viewModel)
                NavigationTab.Notes -> NotesScreen(viewModel)
                NavigationTab.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
private fun BottomTab(info: NavigationTabInfo, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp).testTag(info.testTag)
    ) {
        Box(
            modifier = if (selected) Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).padding(horizontal = 16.dp, vertical = 4.dp) else Modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) info.filledIcon else info.outlinedIcon,
                contentDescription = info.label,
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(info.label, style = MaterialTheme.typography.labelSmall)
    }
}

private enum class NavigationTab { Library, Reading, Notes, Settings }

private data class NavigationTabInfo(
    val tab: NavigationTab,
    val label: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector,
    val testTag: String
)
