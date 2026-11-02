package com.carlosnc.doctordroid.modules.device

import android.app.ActivityManager
import android.content.Context
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Hardware
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.utils.formatSize
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val deviceInfo = uiState.deviceInfo
    val hardwareInfo = uiState.hardwareInfo
    val systemInfo = uiState.systemInfo

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle(text = "Device & System") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            InfoSection(title = "Device Identification")
            Column {
                InfoItem(label = "Model", value = deviceInfo?.model ?: "Unknown", icon = Icons.Outlined.PhoneAndroid)
                InfoItem(label = "Manufacturer", value = deviceInfo?.brand ?: "Unknown", icon = Icons.Outlined.Hardware)
                InfoItem(label = "Device Codename", value = deviceInfo?.deviceName ?: "Unknown", icon = Icons.Outlined.DeveloperMode)
                InfoItem(label = "Hardware / Board", value = Build.BOARD, icon = Icons.Outlined.Memory)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "Android System")
            Column {
                InfoItem(label = "Android Version", value = Build.VERSION.RELEASE, icon = Icons.Outlined.Android)
                InfoItem(label = "API Level", value = "${Build.VERSION.SDK_INT}", icon = Icons.Outlined.DeveloperMode)
                InfoItem(label = "Security Patch", value = Build.VERSION.SECURITY_PATCH, icon = Icons.Outlined.Fingerprint)
                InfoItem(label = "Build Number", value = Build.DISPLAY, icon = Icons.Outlined.DeveloperMode)
                InfoItem(label = "Kernel Version", value = systemInfo.kernelVersion, icon = Icons.Outlined.Speed)
                InfoItem(label = "System Uptime", value = systemInfo.uptime, icon = Icons.Outlined.Timer)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "Hardware Specifications")
            Column {
                InfoItem(label = "Total RAM", value = hardwareInfo.ramSize, icon = Icons.Outlined.Memory)
                InfoItem(label = "Internal Storage", value = hardwareInfo.storage, icon = Icons.Outlined.SdStorage)
                InfoItem(label = "Screen Resolution", value = hardwareInfo.display, icon = Icons.Outlined.DisplaySettings)
                InfoItem(label = "Display Refresh Rate", value = hardwareInfo.refreshRate, icon = Icons.Outlined.Refresh)
                InfoItem(label = "Camera Hardware", value = hardwareInfo.camera, icon = Icons.Outlined.CameraAlt)
                InfoItem(label = "SIM Configuration", value = hardwareInfo.simSlots, icon = Icons.Outlined.SimCard)
                InfoItem(label = "NFC Support", value = hardwareInfo.nfc, icon = Icons.Outlined.Nfc)
                InfoItem(label = "Sensors Count", value = "${hardwareInfo.sensorCount} sensors", icon = Icons.Outlined.Sensors)
            }

            Box(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: ImageVector) {
    DashboardListItem(
        title = label,
        subtitle = value,
        leftIcon = icon,
        rightIcon = null,
        onClick = {}
    )
}

@Composable
fun InfoSection(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}

data class HardwareInfo(
    val ramSize: String,
    val storage: String,
    val display: String,
    val refreshRate: String,
    val camera: String,
    val nfc: String,
    val simSlots: String,
    val sensorCount: String
)

data class SystemInfo(
    val kernelVersion: String,
    val uptime: String
)

fun getHardwareInfo(context: Context): HardwareInfo {
    // RAM
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    val totalRam = formatSize(memInfo.totalMem)

    // Storage
    val stat = StatFs(Environment.getDataDirectory().path)
    val totalStorage = formatSize(stat.totalBytes)

    // Display
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayInfo: String
    val refreshRate: String
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val densityDpi = context.resources.configuration.densityDpi
        displayInfo = "${bounds.width()} x ${bounds.height()} ($densityDpi dpi)"
        val display = context.display
        refreshRate = "${display?.refreshRate?.toInt() ?: 60} Hz"
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(metrics)
        displayInfo = "${metrics.widthPixels} x ${metrics.heightPixels} (${metrics.densityDpi} dpi)"
        refreshRate = "${display.refreshRate.toInt()} Hz"
    }

    // Camera
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIds = try { cameraManager.cameraIdList.size } catch (_: Exception) { 0 }
    val cameraInfo = "$cameraIds cameras detected"

    // NFC
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val nfcInfo = if (nfcAdapter != null) "Supported" else "Not Supported"

    // SIM Slots
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val simCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        telephonyManager?.supportedModemCount ?: 1
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        telephonyManager?.phoneCount ?: 1
    } else {
        1
    }
    val simInfo = if (simCount > 1) "Dual SIM ($simCount slots)" else "Single SIM"

    // Sensors
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensorCount = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL).size.toString()

    return HardwareInfo(
        ramSize = totalRam,
        storage = totalStorage,
        display = displayInfo,
        refreshRate = refreshRate,
        camera = cameraInfo,
        nfc = nfcInfo,
        simSlots = simInfo,
        sensorCount = sensorCount
    )
}

fun getSystemInfo(): SystemInfo {
    val uptimeMillis = SystemClock.elapsedRealtime()
    val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60
    val uptime = when {
        hours > 0 -> String.format(
            Locale.getDefault(),
            "%d hours, %d minutes",
            hours, minutes
        )
        minutes > 0 -> String.format(
            Locale.getDefault(),
            "%d minutes, %d seconds",
            minutes, seconds
        )
        else -> String.format(
            Locale.getDefault(),
            "%d seconds",
            seconds
        )
    }
    
    val kernelVersion = System.getProperty("os.version") ?: "Unknown"

    return SystemInfo(
        kernelVersion = kernelVersion,
        uptime = uptime
    )
}