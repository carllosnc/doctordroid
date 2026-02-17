package com.carlosnc.doctordroid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.R
import com.carlosnc.doctordroid.ui.components.battery.BatteryCard
import com.carlosnc.doctordroid.ui.components.cpu.CpuCard
import com.carlosnc.doctordroid.ui.components.device.DeviceCard
import com.carlosnc.doctordroid.ui.components.memory.MemoryCard
import com.carlosnc.doctordroid.ui.components.storage.StorageCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStorageClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onCpuClick: () -> Unit,
    onDeviceClick: () -> Unit,
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
        Column(modifier = Modifier.padding(innerPadding)) {
            DeviceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                    .padding(16.dp)
                    .clickable { onMemoryClick() }
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
                    .padding(16.dp)
                    .clickable { onCpuClick() }
            )
        }
    }
}
