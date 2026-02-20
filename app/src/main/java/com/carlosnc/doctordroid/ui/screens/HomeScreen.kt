package com.carlosnc.doctordroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.R
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.ui.components.audio.AudioCard
import com.carlosnc.doctordroid.ui.components.battery.BatteryCard
import com.carlosnc.doctordroid.ui.components.camera.CameraCard
import com.carlosnc.doctordroid.ui.components.cpu.CpuCard
import com.carlosnc.doctordroid.ui.components.device.DeviceCard
import com.carlosnc.doctordroid.ui.components.device.rememberDeviceInfo
import com.carlosnc.doctordroid.ui.components.gpu.GpuCard
import com.carlosnc.doctordroid.ui.components.health.HealthCheckCard
import com.carlosnc.doctordroid.ui.components.memory.MemoryCard
import com.carlosnc.doctordroid.ui.components.network.NetworkCard
import com.carlosnc.doctordroid.ui.components.storage.StorageCard
import com.carlosnc.doctordroid.ui.components.temperature.TemperatureCard

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
    onToggleFloatingMonitor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceInfo = rememberDeviceInfo()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    PageTitle(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = onQuickControlClick) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Quick Controls",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onToggleFloatingMonitor) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Toggle Floating Monitor",
                            tint = if (isMonitorActive) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (deviceInfo == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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

                MemoryCard(
                    modifier = cardModifier
                        .clickable { onMemoryClick() }
                )

                NetworkCard(
                    modifier = cardModifier
                        .clickable { onNetworkClick() }
                )

                AudioCard(
                    onClick = onAudioClick,
                    modifier = cardModifier
                )

                BatteryCard(
                    modifier = cardModifier
                        .clickable { onBatteryClick() }
                )

                CpuCard(
                    modifier = cardModifier
                        .clickable { onCpuClick() }
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
                    modifier = cardModifier
                        .clickable { onTemperatureClick() }
                )

                // Extra padding at the bottom for scrolling
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
