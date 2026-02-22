package com.carlosnc.doctordroid.modules.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.PageTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCheckScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTouchTest by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                   title = { PageTitle("Health check") },
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
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoSection(
                    title = "Sensor Health",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SensorCheckList()

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                InfoSection(
                    title = "Hardware Diagnostics",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HardwareTestList(onShowTouchTest = { showTouchTest = true })
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showTouchTest) {
            TouchTestOverlay(
                onDismiss = { showTouchTest = false },
                onComplete = {
                    showTouchTest = false
                    showSuccessDialog = true
                }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Test Complete") },
                text = { Text("Your touch screen is working perfectly across all areas.") },
                confirmButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("Awesome")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun InfoSection(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 8.dp)
    )
}
