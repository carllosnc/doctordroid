package com.carlosnc.doctordroid

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.carlosnc.doctordroid.services.FloatingMonitorService
import com.carlosnc.doctordroid.ui.navigation.AppNavHost
import com.carlosnc.doctordroid.ui.theme.DoctordroidTheme

class MainActivity : ComponentActivity() {

    private var isMonitorActive by mutableStateOf(false)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                toggleFloatingMonitor()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        isMonitorActive = isServiceRunning(FloatingMonitorService::class.java)
        
        setContent {
            DoctordroidTheme {
                MainScreen(
                    isMonitorActive = isMonitorActive,
                    onToggleFloatingMonitor = {
                        if (isMonitorActive) {
                            stopFloatingMonitor()
                        } else {
                            checkOverlayPermissionAndStart()
                        }
                    }
                )
            }
        }
    }

    private fun checkOverlayPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayPermissionLauncher.launch(intent)
            } else {
                toggleFloatingMonitor()
            }
        } else {
            toggleFloatingMonitor()
        }
    }

    private fun toggleFloatingMonitor() {
        if (isServiceRunning(FloatingMonitorService::class.java)) {
            stopFloatingMonitor()
        } else {
            startFloatingMonitor()
        }
    }

    private fun startFloatingMonitor() {
        val intent = Intent(this, FloatingMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        isMonitorActive = true
    }

    private fun stopFloatingMonitor() {
        val intent = Intent(this, FloatingMonitorService::class.java)
        stopService(intent)
        isMonitorActive = false
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}

@Composable
fun MainScreen(
    isMonitorActive: Boolean,
    onToggleFloatingMonitor: () -> Unit
) {
    val navController = rememberNavController()
    AppNavHost(
        navController = navController,
        isMonitorActive = isMonitorActive,
        onToggleFloatingMonitor = onToggleFloatingMonitor
    )
}
