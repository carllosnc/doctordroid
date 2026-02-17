package com.carlosnc.doctordroid.ui.components.storage

import android.os.Environment
import android.os.StatFs
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storageInfo = remember { getStorageInfo() }
    val fileTypeUsage = remember { getFileTypeUsage() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Storage Info") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StorageUsageCard(storageInfo)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StorageDetailsList(storageInfo)

            Spacer(modifier = Modifier.height(24.dp))

            FileTypeUsageList(fileTypeUsage, storageInfo.totalBytes)
        }
    }
}

@Composable
fun StorageUsageCard(storageInfo: StorageInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Internal Storage Usage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StorageDonutChart(
                usedPercentage = storageInfo.usedPercentage,
                modifier = Modifier.size(180.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UsageIndicator(
                    label = "Used",
                    value = formatSize(storageInfo.usedBytes),
                    color = MaterialTheme.colorScheme.primary
                )
                UsageIndicator(
                    label = "Free",
                    value = formatSize(storageInfo.freeBytes),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun StorageDonutChart(usedPercentage: Float, modifier: Modifier = Modifier) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) usedPercentage else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "StorageDonutAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            
            // Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Usage
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedPercentage,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Used",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UsageIndicator(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StorageDetailsList(storageInfo: StorageInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        DetailItem(label = "Mount Point", value = storageInfo.path)
        DetailItem(label = "Total Capacity", value = formatSize(storageInfo.totalBytes))
        DetailItem(label = "Available Space", value = formatSize(storageInfo.freeBytes))
        DetailItem(label = "Used Space", value = formatSize(storageInfo.usedBytes))
        DetailItem(
            label = "Free Percentage", 
            value = String.format(Locale.getDefault(), "%.1f%%", (1f - storageInfo.usedPercentage) * 100)
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyLarge, 
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FileTypeUsageList(fileTypes: List<FileTypeInfo>, totalBytes: Long) {
    val sortedFileTypes = remember(fileTypes) {
        fileTypes.sortedByDescending { it.sizeBytes }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Usage by Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            sortedFileTypes.forEach { fileType ->
                FileTypeItem(fileType, totalBytes)
            }
        }
    }
}

@Composable
fun FileTypeItem(fileType: FileTypeInfo, totalBytes: Long) {
    var animationPlayed by remember { mutableStateOf(false) }
    val targetProgress = if (totalBytes > 0) fileType.sizeBytes.toFloat() / totalBytes.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) targetProgress else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "FileTypeProgressAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fileType.icon,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = fileType.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatSize(fileType.sizeBytes),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = primaryColor,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

data class FileTypeInfo(
    val name: String,
    val sizeBytes: Long,
    val icon: ImageVector
)

fun getFileTypeUsage(): List<FileTypeInfo> {
    return listOf(
        FileTypeInfo("Apps", 32L * 1024 * 1024 * 1024, Icons.Default.Apps),
        FileTypeInfo("Images", 15L * 1024 * 1024 * 1024, Icons.Default.Image),
        FileTypeInfo("Videos", 25L * 1024 * 1024 * 1024, Icons.Default.Movie),
        FileTypeInfo("Audio", 5L * 1024 * 1024 * 1024, Icons.Default.MusicNote),
        FileTypeInfo("Documents", 2L * 1024 * 1024 * 1024, Icons.Default.Description),
        FileTypeInfo("Other", 8L * 1024 * 1024 * 1024, Icons.Default.Folder)
    )
}

data class StorageInfo(
    val path: String,
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val usedPercentage: Float
)

fun getStorageInfo(): StorageInfo {
    val path = Environment.getDataDirectory().path
    val stat = StatFs(path)
    val totalBytes = stat.totalBytes
    val freeBytes = stat.availableBytes
    val usedBytes = totalBytes - freeBytes
    val usedPercentage = if (totalBytes > 0) usedBytes.toFloat() / totalBytes.toFloat() else 0f
    
    return StorageInfo(path, totalBytes, freeBytes, usedBytes, usedPercentage)
}

fun formatSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
        mb >= 1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
        else -> String.format(Locale.getDefault(), "%.2f KB", kb)
    }
}
