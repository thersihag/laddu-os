// Path: app/src/main/java/com/laddu/os/launcher/LauncherViewModel.kt
package com.laddu.os.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LauncherTheme { LIGHT, DARK, LADDU }

class LauncherViewModel(private val context: Context) : ViewModel() {
    private val repository = AppRepository(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("LadduOSPrefs", Context.MODE_PRIVATE)

    private val _installedApps = MutableStateFlow<List<AppModel>>(emptyList())
    val installedApps: StateFlow<List<AppModel>> = _installedApps.asStateFlow()

    private val _recentApps = MutableStateFlow<List<AppModel>>(emptyList())
    val recentApps: StateFlow<List<AppModel>> = _recentApps.asStateFlow()

    private val _dockApps = MutableStateFlow<List<AppModel>>(emptyList())
    val dockApps: StateFlow<List<AppModel>> = _dockApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentTheme = MutableStateFlow(LauncherTheme.LADDU)
    val currentTheme: StateFlow<LauncherTheme> = _currentTheme.asStateFlow()

    private val _isAppDrawerOpen = MutableStateFlow(false)
    val isAppDrawerOpen: StateFlow<Boolean> = _isAppDrawerOpen.asStateFlow()

    init {
        loadInstalledApplications()
        val themeString = prefs.getString("selected_theme", LauncherTheme.LADDU.name) ?: LauncherTheme.LADDU.name
        _currentTheme.value = LauncherTheme.valueOf(themeString)
    }

    fun loadInstalledApplications() {
        viewModelScope.launch {
            val apps = repository.getInstalledApps()
            _installedApps.value = apps
            _dockApps.value = apps.take(minOf(apps.size, 4))
            loadRecentAppsList(apps)
        }
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun toggleAppDrawer(open: Boolean) { _isAppDrawerOpen.value = open }
    fun setTheme(theme: LauncherTheme) {
        _currentTheme.value = theme
        prefs.edit().putString("selected_theme", theme.name).apply()
    }

    fun launchApplication(app: AppModel) {
        performHapticFeedback()
        val intent = Intent().apply {
            component = ComponentName(app.packageName, app.className)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        try {
            context.startActivity(intent)
            saveToRecentList(app)
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun uninstallApplication(packageName: String) {
        performHapticFeedback()
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openApplicationDetails(packageName: String) {
        performHapticFeedback()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun saveToRecentList(app: AppModel) {
        val currentRecents = _recentApps.value.toMutableList().apply {
            remove(app)
            add(0, app)
        }
        val limited = currentRecents.take(5)
        _recentApps.value = limited
        prefs.edit().putString("recent_apps_csv", limited.joinToString(",") { it.packageName }).apply()
    }

    private fun loadRecentAppsList(allApps: List<AppModel>) {
        val savedCsv = prefs.getString("recent_apps_csv", "") ?: ""
        if (savedCsv.isNotEmpty()) {
            _recentApps.value = savedCsv.split(",").mapNotNull { pkg -> allApps.find { it.packageName == pkg } }
        }
    }

    fun performHapticFeedback() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
