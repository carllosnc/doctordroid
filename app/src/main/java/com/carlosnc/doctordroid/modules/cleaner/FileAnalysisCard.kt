package com.carlosnc.doctordroid.modules.cleaner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun FileAnalysisCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "File Analysis",
        subtitle = "Scan storage by file type",
        leftIcon = Icons.Default.CleaningServices,
        onClick = onClick,
        modifier = modifier
    )
}
