package com.carlosnc.doctordroid.modules.health

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosnc.doctordroid.ui.components.PageTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCheckScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HealthCheckViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { PageTitle("Health & Diagnostics") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Hardware Sensors
                Text(
                    text = "HARDWARE SENSORS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                SensorCheckList(
                    sensorList = uiState.sensors,
                    onSensorClick = { targetTab ->
                        viewModel.openSensorLab(targetTab)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Interactive Hardware Diagnostics
                Text(
                    text = "COMPONENT TESTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                HardwareTestList(
                    onShowTouchTest = { viewModel.setTouchTestVisible(true) },
                    onShowDeadPixelTest = { viewModel.setDeadPixelTestVisible(true) }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Test Overlays
        if (uiState.showTouchTest) {
            TouchTestOverlay(
                onDismiss = { viewModel.setTouchTestVisible(false) },
                onComplete = {
                    viewModel.setTouchTestVisible(false)
                    viewModel.setSuccessDialogVisible(true)
                }
            )
        }

        if (uiState.showDeadPixelTest) {
            DeadPixelTestOverlay(
                onDismiss = { viewModel.setDeadPixelTestVisible(false) }
            )
        }

        if (uiState.showSensorLab) {
            InteractiveSensorTestOverlay(
                initialTab = uiState.sensorLabInitialTab,
                onDismiss = { viewModel.closeSensorLab() }
            )
        }

        if (uiState.showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setSuccessDialogVisible(false) },
                title = { Text("Touch Test Complete") },
                text = { Text("Your touch screen is responding accurately across all grid coordinates.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.setSuccessDialogVisible(false) }) {
                        Text("Done")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            )
        }
    }
}
