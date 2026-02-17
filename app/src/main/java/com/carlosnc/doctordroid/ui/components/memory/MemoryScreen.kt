package com.carlosnc.doctordroid.ui.components.memory

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var memoryInfo by remember { mutableStateOf(getMemoryInfo(context)) }
    var appMemoryList by remember { mutableStateOf(listOf<AppMemoryInfo>()) }

    LaunchedEffect(Unit) {
        while(true) {
            memoryInfo = getMemoryInfo(context)
            appMemoryList = getRunningAppsMemory(context, memoryInfo.usedMem)
            delay(3000) // Update every 3 seconds
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Memory Details") },
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
            MemoryUsageCard(memoryInfo)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MemoryDetailsList(memoryInfo)

            Spacer(modifier = Modifier.height(32.dp))

            AppMemoryUsageSection(appMemoryList, memoryInfo.totalMem)
        }
    }
}

@Composable
fun MemoryUsageCard(memoryInfo: MemoryInfo) {
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
                text = "RAM Usage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MemoryDonutChart(
                usedPercentage = memoryInfo.usedPercentage,
                modifier = Modifier.size(180.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MemoryUsageIndicator(
                    label = "Used",
                    value = formatSize(memoryInfo.usedMem),
                    color = MaterialTheme.colorScheme.primary
                )
                MemoryUsageIndicator(
                    label = "Available",
                    value = formatSize(memoryInfo.availMem),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun MemoryDonutChart(usedPercentage: Float, modifier: Modifier = Modifier) {
    val animatedPercentage by animateFloatAsState(
        targetValue = usedPercentage,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "MemoryDonutAnimation"
    )

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
fun MemoryUsageIndicator(label: String, value: String, color: Color) {
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
fun MemoryDetailsList(memoryInfo: MemoryInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        MemoryDetailItem(label = "Total RAM", value = formatSize(memoryInfo.totalMem))
        MemoryDetailItem(label = "Used RAM", value = formatSize(memoryInfo.usedMem))
        MemoryDetailItem(label = "Available RAM", value = formatSize(memoryInfo.availMem))
        MemoryDetailItem(label = "Threshold", value = formatSize(memoryInfo.threshold))
        MemoryDetailItem(
            label = "Low Memory System", 
            value = if (memoryInfo.lowMemory) "Yes" else "No"
        )
    }
}

@Composable
fun MemoryDetailItem(label: String, value: String) {
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
fun AppMemoryUsageSection(apps: List<AppMemoryInfo>, totalRam: Long) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "App Consumption",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        apps.forEach { app ->
            AppMemoryItem(app, totalRam)
        }
    }
}

@Composable
fun AppMemoryItem(app: AppMemoryInfo, totalRam: Long) {
    val progress = if (totalRam > 0) app.memoryBytes.toFloat() / totalRam.toFloat() else 0f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = formatSize(app.memoryBytes),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

data class MemoryInfo(
    val totalMem: Long,
    val availMem: Long,
    val usedMem: Long,
    val threshold: Long,
    val lowMemory: Boolean,
    val usedPercentage: Float
)

data class AppMemoryInfo(
    val appName: String,
    val packageName: String,
    val memoryBytes: Long,
    val icon: Drawable?
)

fun getMemoryInfo(context: Context): MemoryInfo {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    
    val totalMem = memoryInfo.totalMem
    val availMem = memoryInfo.availMem
    val usedMem = totalMem - availMem
    val usedPercentage = if (totalMem > 0) usedMem.toFloat() / totalMem.toFloat() else 0f
    
    return MemoryInfo(
        totalMem = totalMem,
        availMem = availMem,
        usedMem = usedMem,
        threshold = memoryInfo.threshold,
        lowMemory = memoryInfo.lowMemory,
        usedPercentage = usedPercentage
    )
}

suspend fun getRunningAppsMemory(context: Context, totalUsedBytes: Long): List<AppMemoryInfo> = withContext(Dispatchers.Default) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val pm = context.packageManager
    
    val runningProcesses = activityManager.runningAppProcesses ?: emptyList()
    val pids = runningProcesses.map { it.pid }.toIntArray()
    
    val list = mutableListOf<AppMemoryInfo>()
    var accountedMemory = 0L

    if (pids.isNotEmpty()) {
        val memoryInfos = activityManager.getProcessMemoryInfo(pids)
        runningProcesses.forEachIndexed { index, process ->
            val pss = if (index < memoryInfos.size) memoryInfos[index].totalPss.toLong() * 1024 else 0L
            
            if (pss > 0) {
                val appLabel = try {
                    val appInfo = pm.getApplicationInfo(process.processName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    process.processName
                }
                
                val icon = try {
                    pm.getApplicationIcon(process.processName)
                } catch (e: Exception) {
                    null
                }
                
                list.add(AppMemoryInfo(appLabel, process.processName, pss, icon))
                accountedMemory += pss
            }
        }
    }
    
    val systemMemory = totalUsedBytes - accountedMemory
    if (systemMemory > 0) {
        list.add(AppMemoryInfo("System & Other Apps", "system", systemMemory, null))
    }

    return@withContext list.sortedByDescending { it.memoryBytes }
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
