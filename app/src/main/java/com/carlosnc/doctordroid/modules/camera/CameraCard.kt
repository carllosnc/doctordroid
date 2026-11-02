package com.carlosnc.doctordroid.modules.camera

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun CameraCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Camera Info",
        subtitle = "Resolution & features",
        leftIcon = Icons.Outlined.CameraAlt,
        onClick = onClick,
        modifier = modifier
    )
}
