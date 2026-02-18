package com.carlosnc.doctordroid.ui.components.device

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = "Device",
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            val text = deviceInfo?.let { "${it.brand} ${it.model}" } ?: "Information"
            Text(text = text)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
