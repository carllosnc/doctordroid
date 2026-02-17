package com.carlosnc.doctordroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.R
import com.carlosnc.doctordroid.ui.components.applications.ApplicationsCard
import com.carlosnc.doctordroid.ui.components.battery.BatteryCard
import com.carlosnc.doctordroid.ui.components.cpu.CpuCard
import com.carlosnc.doctordroid.ui.components.device.DeviceCard
import com.carlosnc.doctordroid.ui.components.memory.MemoryCard
import com.carlosnc.doctordroid.ui.components.network.NetworkCard
import com.carlosnc.doctordroid.ui.components.storage.StorageCard
import com.carlosnc.doctordroid.ui.components.temperature.TemperatureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStorageClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onCpuClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onTemperatureClick: () -> Unit,
    onApplicationsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeviceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onDeviceClick() }
            )
            StorageCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onStorageClick() }
            )
            MemoryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onMemoryClick() }
            )
            NetworkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNetworkClick() }
            )
            BatteryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onBatteryClick() }
            )
            CpuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onCpuClick() }
            )
            TemperatureCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onTemperatureClick() }
            )
            ApplicationsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onApplicationsClick() }
            )
            // Extra padding at the bottom for scrolling
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
