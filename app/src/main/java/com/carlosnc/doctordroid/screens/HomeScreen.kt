package com.carlosnc.doctordroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.R
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.modules.audio.AudioCard
import com.carlosnc.doctordroid.modules.battery.BatteryCard
import com.carlosnc.doctordroid.modules.camera.CameraCard
import com.carlosnc.doctordroid.modules.cpu.CpuCard
import com.carlosnc.doctordroid.modules.device.DeviceCard
import com.carlosnc.doctordroid.modules.gpu.GpuCard
import com.carlosnc.doctordroid.modules.health.HealthCheckCard
import com.carlosnc.doctordroid.modules.memory.MemoryCard
import com.carlosnc.doctordroid.modules.network.NetworkCard
import com.carlosnc.doctordroid.modules.storage.StorageCard
import com.carlosnc.doctordroid.modules.temperature.TemperatureCard
import com.carlosnc.doctordroid.modules.cleaner.FileAnalysisCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isMonitorActive: Boolean,
    onStorageClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onCpuClick: () -> Unit,
    onGpuClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onTemperatureClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onAudioClick: () -> Unit,
    onQuickControlClick: () -> Unit,
    onHealthCheckClick: () -> Unit,
    onDeviceResumeClick: () -> Unit,
    onFileAnalysisClick: () -> Unit,
    onToggleFloatingMonitor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    PageTitle(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = onDeviceResumeClick) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "Device Resume",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onQuickControlClick) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Quick Controls",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onToggleFloatingMonitor) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = "Toggle Floating Monitor",
                            tint = if (isMonitorActive) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            val cardModifier = Modifier.fillMaxWidth()

            HealthCheckCard(
                    onClick = onHealthCheckClick,
                    modifier = cardModifier
                )

                DeviceCard(
                    onClick = onDeviceClick,
                    modifier = cardModifier
                )

                StorageCard(
                    onClick = onStorageClick,
                    modifier = cardModifier
                )

                FileAnalysisCard(
                    onClick = onFileAnalysisClick,
                    modifier = cardModifier
                )

                MemoryCard(
                    onClick = onMemoryClick,
                    modifier = cardModifier
                )

                NetworkCard(
                    onClick = onNetworkClick,
                    modifier = cardModifier
                )

                AudioCard(
                    onClick = onAudioClick,
                    modifier = cardModifier
                )

                BatteryCard(
                    onClick = onBatteryClick,
                    modifier = cardModifier
                )

                CpuCard(
                    onClick = onCpuClick,
                    modifier = cardModifier
                )

                GpuCard(
                    onClick = onGpuClick,
                    modifier = cardModifier
                )

                CameraCard(
                    onClick = onCameraClick,
                    modifier = cardModifier
                )

                TemperatureCard(
                    onClick = onTemperatureClick,
                    modifier = cardModifier
                )

                // Extra padding at the bottom for scrolling
                Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
