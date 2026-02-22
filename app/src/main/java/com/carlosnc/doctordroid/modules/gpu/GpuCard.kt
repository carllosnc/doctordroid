package com.carlosnc.doctordroid.modules.gpu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun GpuCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "GPU & Graphics",
        subtitle = "Renderer & graphics info",
        leftIcon = Icons.Default.GraphicEq,
        onClick = onClick,
        modifier = modifier
    )
}
