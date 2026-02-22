package com.carlosnc.doctordroid.modules.device

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DeviceInfo(
    val deviceName: String,
    val brand: String,
    val model: String
)

@Composable
fun rememberDeviceInfo(): DeviceInfo? {
    var deviceInfo by remember { mutableStateOf<DeviceInfo?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val deviceName = Build.DEVICE
            val brand = Build.MANUFACTURER
            val model = Build.MODEL
            deviceInfo = DeviceInfo(deviceName, brand, model)
        }
    }

    return deviceInfo
}

@Composable
fun DeviceCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val deviceInfo = rememberDeviceInfo()
    val subtitle = deviceInfo?.let { "${it.brand} ${it.model}" } ?: "Information"

    DashboardListItem(
        title = "Device",
        subtitle = subtitle,
        leftIcon = Icons.Default.PhoneAndroid,
        onClick = onClick,
        modifier = modifier
    )
}
