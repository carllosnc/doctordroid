package com.carlosnc.doctordroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DashboardListItem(
    title: String,
    subtitle: String,
    leftIcon: ImageVector,
    modifier: Modifier = Modifier,
    rightIcon: ImageVector? = Icons.Default.ChevronRight,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val trailing: @Composable (() -> Unit)? = when {
        trailingContent != null -> trailingContent
        rightIcon != null -> {
            {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            }
        }
        else -> null
    }

    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            ListIcon(icon = leftIcon)
        },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
