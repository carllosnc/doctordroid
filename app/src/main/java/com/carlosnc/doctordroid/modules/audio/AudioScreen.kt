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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AudioViewModel = viewModel()
) {
    val audioInfo by viewModel.audioInfo.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle(text = "Audio & Sound") },
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
            InfoSection(title = "Volume Levels")

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(
                    label = "Media Volume",
                    value = "${audioInfo.musicVolume} / ${audioInfo.musicMaxVolume}",
                    icon = Icons.Outlined.MusicNote
                )
                DetailRow(
                    label = "Ring Volume",
                    value = "${audioInfo.ringVolume} / ${audioInfo.ringMaxVolume}",
                    icon = Icons.Outlined.NotificationsActive
                )
                DetailRow(
                    label = "Notification Volume",
                    value = "${audioInfo.notificationVolume} / ${audioInfo.notificationMaxVolume}",
                    icon = Icons.Outlined.Notifications
                )
                DetailRow(
                    label = "Alarm Volume",
                    value = "${audioInfo.alarmVolume} / ${audioInfo.alarmMaxVolume}",
                    icon = Icons.Outlined.Alarm
                )
                DetailRow(
                    label = "System Volume",
                    value = "${audioInfo.systemVolume} / ${audioInfo.systemMaxVolume}",
                    icon = Icons.Outlined.SettingsSuggest
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "Audio Features")

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(
                    label = "Ringer Mode",
                    value = audioInfo.ringerMode,
                    icon = Icons.AutoMirrored.Outlined.VolumeUp
                )
                DetailRow(
                    label = "Microphone",
                    value = if (audioInfo.isMicrophoneOn) "Available" else "Muted",
                    icon = Icons.Outlined.Mic
                )
                DetailRow(
                    label = "Music Active",
                    value = if (audioInfo.isMusicActive) "Yes" else "No",
                    icon = Icons.Outlined.GraphicEq
                )
                DetailRow(
                    label = "Speakerphone",
                    value = if (audioInfo.isSpeakerphoneOn) "Enabled" else "Disabled",
                    icon = Icons.Outlined.Speaker
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DetailRow(
                        label = "Volume Fixed",
                        value = if (audioInfo.isVolumeFixed) "Yes" else "No",
                        icon = Icons.AutoMirrored.Outlined.VolumeMute
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
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