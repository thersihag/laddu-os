// Path: app/src/main/java/com/laddu/os/launcher/AppRepository.kt
package com.laddu.os.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledApps(): List<AppModel> = withContext(Dispatchers.IO) {
        val launcherApps = mutableListOf<AppModel>()
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolvedActivities = packageManager.queryIntentActivities(mainIntent, 0)
        val selfPackage = context.packageName

        for (resolveInfo in resolvedActivities) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == selfPackage) continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val className = resolveInfo.activityInfo.name
            val icon = resolveInfo.loadIcon(packageManager)

            launcherApps.add(AppModel(label, packageName, className, icon))
        }
        launcherApps.sortBy { it.label.lowercase() }
        return@withContext launcherApps
    }
}
