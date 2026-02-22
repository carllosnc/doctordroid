package com.carlosnc.doctordroid.modules.audio

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun AudioCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Audio & Sound",
        subtitle = "Volume, sources & info",
        leftIcon = Icons.AutoMirrored.Filled.VolumeUp,
        onClick = onClick,
        modifier = modifier
    )
}
