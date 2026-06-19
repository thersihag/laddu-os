// Path: app/src/main/java/com/laddu/os/launcher/HomeScreen.kt
package com.laddu.os.launcher

import android.widget.TextClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HomeScreen(viewModel: LauncherViewModel, theme: LauncherTheme, onOpenSettings: () -> Unit) {
    val dockApps by viewModel.dockApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    var dragAmountY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = { if (dragAmountY < -120f) { viewModel.performHapticFeedback(); viewModel.toggleAppDrawer(true) }; dragAmountY = 0f },
                onDrag = { change, dragAmount -> change.consume(); dragAmountY += dragAmount.y }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 48.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.9f).height(100.dp).glassyCard(theme).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AndroidView(factory = { TextClock(it).apply { format12Hour = "hh:mm a"; textSize = 28f; setTextColor(android.graphics.Color.GRAY) } })
                    AndroidView(factory = { TextClock(it).apply { format12Hour = "EEEE, MMMM dd"; textSize = 12f; setTextColor(android.graphics.Color.LTGRAY) } })
                }
                IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Palette, "Theme", tint = if(theme==LauncherTheme.DARK) Color.White else Color.Black) }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f).padding(16.dp)
            ) {
                items(installedApps.take(8)) { app ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.launchApplication(app) }.padding(8.dp)) {
                        Image(bitmap = app.imageBitmap, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text(app.label, color = if(theme == LauncherTheme.DARK) Color.White else Color.Black, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Text("Swipe Up for Apps", color = if(theme==LauncherTheme.DARK) Color.White.copy(0.6f) else Color.Black.copy(0.6f), fontSize = 12.sp)
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth().height(85.dp).glassyCard(theme),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.forEach { app ->
                Image(bitmap = app.imageBitmap, contentDescription = null, modifier = Modifier.size(48.dp).clickable { viewModel.launchApplication(app) })
            }
        }
    }
}
