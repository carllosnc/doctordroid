package com.carlosnc.doctordroid.modules.cpu

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpuScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coreCount = Runtime.getRuntime().availableProcessors()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle(text = "CPU & Performance") },
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
        ) {
            InfoSection(title = "Processor Information")
            CpuDetailsList(coreCount)
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            InfoSection(title = "Core Frequencies")
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(coreCount) { index ->
                    CoreFrequencyItem(coreIndex = index)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CpuDetailsList(coreCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CpuDetailItem(label = "Processor", value = Build.HARDWARE, icon = Icons.Default.Speed)
        CpuDetailItem(label = "Cores", value = "$coreCount", icon = Icons.Default.Memory)
        CpuDetailItem(label = "Architecture", value = System.getProperty("os.arch") ?: "Unknown", icon = Icons.Default.Speed)
        CpuDetailItem(label = "ABI", value = Build.SUPPORTED_ABIS.joinToString(", "), icon = Icons.Default.Speed)
    }
}

@Composable
fun CoreFrequencyItem(coreIndex: Int) {
    var frequency by remember { mutableStateOf(getCoreFrequency(coreIndex)) }
    
    LaunchedEffect(Unit) {
        while(true) {
            frequency = getCoreFrequency(coreIndex)
            delay(1000)
        }
    }

    DashboardListItem(
        title = "Core $coreIndex",
        subtitle = if (frequency > 0) "${frequency}MHz" else "Offline",
        leftIcon = Icons.Default.Speed,
        rightIcon = null,
        onClick = {}
    )
}

@Composable
fun CpuDetailItem(label: String, value: String, icon: ImageVector) {
    DashboardListItem(
        title = label,
        subtitle = value,
        leftIcon = icon,
        rightIcon = null,
        onClick = {}
    )
}

@Composable
fun InfoSection(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}
