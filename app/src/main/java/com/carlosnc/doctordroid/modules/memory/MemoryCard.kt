package com.carlosnc.doctordroid.modules.memory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun MemoryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Memory Details",
        subtitle = "RAM & Swap usage",
        leftIcon = Icons.Default.Memory,
        onClick = onClick,
        modifier = modifier
    )
}
