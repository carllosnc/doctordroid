package com.carlosnc.doctordroid.ui.components.audio

import android.content.Context
import android.media.AudioManager
import android.os.Build
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioInfo = remember { getAudioInfo(context) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Audio & Sound") },
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
            AudioSectionHeader(title = "Volume Levels")
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VolumeItem(
                    label = "Media",
                    level = audioInfo.musicVolume,
                    max = audioInfo.musicMaxVolume,
                    icon = Icons.Default.MusicNote
                )
                VolumeItem(
                    label = "Ring",
                    level = audioInfo.ringVolume,
                    max = audioInfo.ringMaxVolume,
                    icon = Icons.Default.NotificationsActive
                )
                VolumeItem(
                    label = "Notification",
                    level = audioInfo.notificationVolume,
                    max = audioInfo.notificationMaxVolume,
                    icon = Icons.Default.Notifications
                )
                VolumeItem(
                    label = "Alarm",
                    level = audioInfo.alarmVolume,
                    max = audioInfo.alarmMaxVolume,
                    icon = Icons.Default.Alarm
                )
                VolumeItem(
                    label = "System",
                    level = audioInfo.systemVolume,
                    max = audioInfo.systemMaxVolume,
                    icon = Icons.Default.SettingsSuggest
                )
            }

            AudioSectionHeader(title = "Audio Features")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(
                    label = "Ringer Mode",
                    value = audioInfo.ringerMode,
                    icon = Icons.Default.VolumeUp
                )
                DetailRow(
                    label = "Microphone",
                    value = if (audioInfo.isMicrophoneOn) "Available" else "Muted",
                    icon = Icons.Default.Mic
                )
                DetailRow(
                    label = "Music Active",
                    value = if (audioInfo.isMusicActive) "Yes" else "No",
                    icon = Icons.Default.GraphicEq
                )
                DetailRow(
                    label = "Speakerphone",
                    value = if (audioInfo.isSpeakerphoneOn) "Enabled" else "Disabled",
                    icon = Icons.Default.Speaker
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DetailRow(
                        label = "Volume Fixed",
                        value = if (audioInfo.isVolumeFixed) "Yes" else "No",
                        icon = Icons.Default.VolumeMute
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AudioSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
fun VolumeItem(label: String, level: Int, max: Int, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$level / $max",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { level.toFloat() / max.coerceAtLeast(1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

data class AudioInfo(
    val musicVolume: Int,
    val musicMaxVolume: Int,
    val ringVolume: Int,
    val ringMaxVolume: Int,
    val notificationVolume: Int,
    val notificationMaxVolume: Int,
    val alarmVolume: Int,
    val alarmMaxVolume: Int,
    val systemVolume: Int,
    val systemMaxVolume: Int,
    val ringerMode: String,
    val isMicrophoneOn: Boolean,
    val isMusicActive: Boolean,
    val isSpeakerphoneOn: Boolean,
    val isVolumeFixed: Boolean
)

fun getAudioInfo(context: Context): AudioInfo {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    val ringerMode = when (audioManager.ringerMode) {
        AudioManager.RINGER_MODE_NORMAL -> "Normal"
        AudioManager.RINGER_MODE_SILENT -> "Silent"
        AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
        else -> "Unknown"
    }

    return AudioInfo(
        musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
        musicMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
        ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING),
        ringMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
        notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
        notificationMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
        alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM),
        alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
        systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM),
        systemMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
        ringerMode = ringerMode,
        isMicrophoneOn = audioManager.isMicrophoneMute.not(),
        isMusicActive = audioManager.isMusicActive,
        isSpeakerphoneOn = audioManager.isSpeakerphoneOn,
        isVolumeFixed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) audioManager.isVolumeFixed else false
    )
}
