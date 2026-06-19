// Path: app/src/main/java/com/laddu/os/launcher/ThemeSettingsSheet.kt
package com.laddu.os.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(viewModel: LauncherViewModel, currentTheme: LauncherTheme, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Laddu OS Customizer", fontSize = 18.sp, maxLines = 1)
            Spacer(Modifier.height(16.dp))
            LauncherTheme.values().forEach { theme ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if(theme == currentTheme) Color.LightGray else Color.Transparent)
                        .clickable { viewModel.setTheme(theme) },
                    contentAlignment = Alignment.Center
                ) { Text(theme.name, fontSize = 14.sp) }
            }
        }
    }
}
