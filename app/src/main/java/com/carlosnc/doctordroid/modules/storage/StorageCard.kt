package com.carlosnc.doctordroid.modules.storage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun StorageCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storageInfo = remember { getStorageInfo() }
    val usedFormatted = formatSize(storageInfo.usedBytes)
    val totalFormatted = formatSize(storageInfo.totalBytes)
    val percentUsed = (storageInfo.usedPercentage * 100).toInt()

    DashboardListItem(
        title = "Storage Details",
        subtitle = "$usedFormatted used of $totalFormatted ($percentUsed%)",
        leftIcon = Icons.Default.SdCard,
        onClick = onClick,
        modifier = modifier
    )
}
