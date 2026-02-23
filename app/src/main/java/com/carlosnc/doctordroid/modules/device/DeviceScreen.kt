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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hardwareInfo = remember { getHardwareInfo(context) }
    val systemInfo = getSystemInfo()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
               title = { PageTitle("Device info") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            InfoSection(title = "Hardware")
            HardwareInfoContent(hardwareInfo)
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            InfoSection(title = "System")
            SystemInfoContent(systemInfo)
            
            // Add some bottom padding for better scroll experience
            Box(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HardwareInfoContent(hardwareInfo: HardwareInfo) {
    Column {
        InfoItem(label = "RAM Size", value = hardwareInfo.ramSize, icon = Icons.Default.Memory)
        InfoItem(label = "Internal Storage", value = hardwareInfo.storage, icon = Icons.Default.SdCard)
        InfoItem(label = "Display", value = hardwareInfo.display, icon = Icons.Default.DisplaySettings)
        InfoItem(label = "Refresh Rate", value = hardwareInfo.refreshRate, icon = Icons.Default.Refresh)
        InfoItem(label = "Cameras", value = hardwareInfo.camera, icon = Icons.Default.CameraAlt)
        InfoItem(label = "NFC Support", value = hardwareInfo.nfc, icon = Icons.Default.Nfc)
        InfoItem(label = "SIM Slots", value = hardwareInfo.simSlots, icon = Icons.Default.SimCard)
        InfoItem(label = "Sensors Count", value = hardwareInfo.sensorCount, icon = Icons.Default.Sensors)
    }
}

@Composable
fun SystemInfoContent(systemInfo: SystemInfo) {
    Column {
        InfoItem(label = "Manufacturer", value = Build.MANUFACTURER, icon = Icons.Default.Hardware)
        InfoItem(label = "Model", value = Build.MODEL, icon = Icons.Default.PhoneAndroid)
        InfoItem(label = "Board", value = Build.BOARD, icon = Icons.Default.DeveloperMode)
        InfoItem(label = "Hardware", value = Build.HARDWARE, icon = Icons.Default.Hardware)
        InfoItem(label = "Android Version", value = Build.VERSION.RELEASE, icon = Icons.Default.Android)
        InfoItem(label = "Security Patch", value = Build.VERSION.SECURITY_PATCH, icon = Icons.Default.Security)
        InfoItem(label = "SDK Level", value = Build.VERSION.SDK_INT.toString(), icon = Icons.Default.Fingerprint)
        InfoItem(label = "Bootloader", value = Build.BOOTLOADER, icon = Icons.Default.SettingsBackupRestore)
        InfoItem(label = "Kernel Version", value = systemInfo.kernelVersion, icon = Icons.Default.SettingsBackupRestore)
        InfoItem(label = "Uptime", value = systemInfo.uptime, icon = Icons.Default.Timer)
    }
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
    val metrics = DisplayMetrics()
    @Suppress("DEPRECATION")
    val display = windowManager.defaultDisplay
    display.getMetrics(metrics)
    val displayInfo = "${metrics.widthPixels} x ${metrics.heightPixels} (${metrics.densityDpi} dpi)"
    val refreshRate = "${display.refreshRate.toInt()} Hz"

    // Camera
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIds = try { cameraManager.cameraIdList.size } catch (e: Exception) { 0 }
    val cameraInfo = "$cameraIds cameras detected"

    // NFC
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val nfcInfo = if (nfcAdapter != null) "Supported" else "Not Supported"

    // SIM Slots
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        telephonyManager.phoneCount
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
    val uptime = String.format(
        Locale.getDefault(),
        "%d hours, %d minutes",
        TimeUnit.MILLISECONDS.toHours(uptimeMillis),
        TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
    )
    
    val kernelVersion = System.getProperty("os.version") ?: "Unknown"

    return SystemInfo(
        kernelVersion = kernelVersion,
        uptime = uptime
    )
}

fun formatSize(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format(Locale.getDefault(), "%.2f GB", gb)
}
