package com.carlosnc.doctordroid.ui.screens

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.PageTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickControlScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    // Media Volume
    val maxMediaVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    var mediaVolumeScale by remember { mutableFloatStateOf(currentMediaVolume.toFloat() / maxMediaVolume.coerceAtLeast(1)) }
    
    // Notification Volume
    val maxNotifVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
    val currentNotifVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
    var notifVolumeScale by remember { mutableFloatStateOf(currentNotifVolume.toFloat() / maxNotifVolume.coerceAtLeast(1)) }

    // Ring Volume
    val maxRingVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
    val currentRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
    var ringVolumeScale by remember { mutableFloatStateOf(currentRingVolume.toFloat() / maxRingVolume.coerceAtLeast(1)) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle("Quick Controls") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ControlCard(
                title = "Media Volume",
                value = mediaVolumeScale,
                icon = Icons.Default.VolumeUp,
                onValueChange = { newValue ->
                    mediaVolumeScale = newValue
                    val volumeIndex = (newValue * maxMediaVolume).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)
                }
            )

            ControlCard(
                title = "Notification Volume",
                value = notifVolumeScale,
                icon = Icons.Default.Notifications,
                onValueChange = { newValue ->
                    notifVolumeScale = newValue
                    val volumeIndex = (newValue * maxNotifVolume).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volumeIndex, 0)
                }
            )

            ControlCard(
                title = "Ringtone Volume",
                value = ringVolumeScale,
                icon = Icons.Default.Phone,
                onValueChange = { newValue ->
                    ringVolumeScale = newValue
                    val volumeIndex = (newValue * maxRingVolume).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volumeIndex, 0)
                }
            )
        }
    }
}

@Composable
fun ControlCard(
    title: String,
    value: Float,
    icon: ImageVector,
    onValueChange: (Float) -> Unit
) {

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        )
    }
}
