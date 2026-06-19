// Path: app/src/main/java/com/laddu/os/launcher/AppDrawerScreen.kt
package com.laddu.os.launcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerScreen(viewModel: LauncherViewModel, theme: LauncherTheme, onBack: () -> Unit) {
    val installedApps by viewModel.installedApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedApp by remember { mutableStateOf<AppModel?>(null) }

    val filteredApps = installedApps.filter { it.label.contains(searchQuery, true) }

    Box(modifier = Modifier.fillMaxSize().background(if(theme==LauncherTheme.DARK) Color(0xFF0E0818) else Color(0xFFFFF5F9)).padding(top = 32.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search App...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            )

            LazyVerticalGrid(columns = GridCells.Fixed(4), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredApps) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.combinedClickable(onClick = { viewModel.launchApplication(app) }, onLongClick = { viewModel.performHapticFeedback(); selectedApp = app })
                    ) {
                        Image(bitmap = app.imageBitmap, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text(app.label, color = if(theme==LauncherTheme.DARK) Color.White else Color.Black, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        selectedApp?.let { app ->
            AlertDialog(
                onDismissRequest = { selectedApp = null },
                title = { Text(app.label) },
                text = {
                    Column {
                        TextButton(onClick = { viewModel.openApplicationDetails(app.packageName); selectedApp = null }) { Row { Icon(Icons.Default.Info, null); Spacer(Modifier.width(8.dp)); Text("App Info") } }
                        TextButton(onClick = { viewModel.uninstallApplication(app.packageName); selectedApp = null }) { Row { Icon(Icons.Default.DeleteForever, null, tint = Color.Red); Spacer(Modifier.width(8.dp)); Text("Uninstall", color = Color.Red) } }
                    }
                },
                confirmButton = { TextButton(onClick = { selectedApp = null }) { Text("Cancel") } }
            )
        }
    }
}
