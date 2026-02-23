package com.carlosnc.doctordroid.modules.audio

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.ui.components.ProgressListItem

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
                title = { PageTitle(text = "Audio & Sound") },
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
            InfoSection(title = "Volume Levels")
            
            Column(modifier = Modifier.fillMaxWidth()) {
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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "Audio Features")

            Column(modifier = Modifier.fillMaxWidth()) {
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
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
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

@Composable
fun VolumeItem(label: String, level: Int, max: Int, icon: ImageVector) {
    ProgressListItem(
        title = label,
        value = "$level / $max",
        progress = level.toFloat() / max.coerceAtLeast(1),
        icon = icon
    )
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector) {
    DashboardListItem(
        title = label,
        subtitle = value,
        leftIcon = icon,
        rightIcon = null,
        onClick = {}
    )
}
