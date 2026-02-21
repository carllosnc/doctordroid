package com.carlosnc.doctordroid.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.Intent
import androidx.core.content.FileProvider
import android.os.Build
import android.telephony.TelephonyManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.ui.components.device.rememberDeviceInfo
import com.carlosnc.doctordroid.ui.components.storage.getStorageInfo
import com.carlosnc.doctordroid.ui.components.storage.formatSize
import com.carlosnc.doctordroid.ui.components.memory.getMemoryInfo
import com.carlosnc.doctordroid.ui.components.battery.getBatteryInfo
import com.carlosnc.doctordroid.ui.components.network.getNetworkDetails
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceResumeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val deviceInfo = rememberDeviceInfo()
    val storageInfo = remember { getStorageInfo() }
    val memoryInfo = remember { getMemoryInfo(context) }
    val batteryInfo = remember { getBatteryInfo(context) }
    val networkInfo = remember { getNetworkDetails(context) }
    val simInfo = remember { getSimInfo(context) }
    val view = LocalView.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PageTitle("Device Resume") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareResume(context, view)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "DOCTOR DROID",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                ResumeSection(title = "Identification") {
                    ResumeItem("Manufacturer", deviceInfo?.brand ?: "Unknown")
                    ResumeItem("Model", deviceInfo?.model ?: "Unknown")
                    ResumeItem("Android Version", Build.VERSION.RELEASE)
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                ResumeSection(title = "Hardware & Storage") {
                    ResumeItem("Total RAM", formatMemorySize(memoryInfo.totalMem))
                    ResumeItem("Total Storage", formatSize(storageInfo.totalBytes))
                    ResumeItem("Available Space", formatSize(storageInfo.totalBytes - storageInfo.usedBytes))
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                ResumeSection(title = "Connectivity & SIM") {
                    ResumeItem("Network Type", networkInfo.type)
                    if (networkInfo.type == "WiFi") {
                        ResumeItem("WiFi Standard", networkInfo.wifiStandard)
                    }
                    ResumeItem("SIM Config", simInfo)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                ResumeSection(title = "Battery Status") {
                    ResumeItem("Battery Level", "${batteryInfo.level}%")
                    ResumeItem("Battery Health", batteryInfo.health)
                    ResumeItem("Capacity", "${batteryInfo.capacity} mAh")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                DiagnosticBadge()
            }
        }
    }
}

@Composable
fun DiagnosticBadge() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE8F5E9), // Light green background
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DIAGNOSTIC PASSED",
                color = Color(0xFF1B5E20),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                letterSpacing = 1.2.sp
            )
        }
    }
}

private fun getSimInfo(context: Context): String {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        telephonyManager.phoneCount
    } else {
        1
    }
    return if (simCount > 1) "Dual SIM" else "Single SIM"
}

@Composable
fun ResumeSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
fun ResumeItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatMemorySize(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format(Locale.getDefault(), "%.1f GB", gb)
}

fun shareResume(context: Context, view: android.view.View) {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    try {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "device_resume.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        if (contentUri != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Device Resume"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
