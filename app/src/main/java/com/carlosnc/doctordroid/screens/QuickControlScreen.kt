package com.carlosnc.doctordroid.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeMute
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.BrightnessHigh
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val activity = remember(context) { context.findActivity() }
    val audioManager = remember(context) { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val cameraManager = remember(context) { context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager }

    // Flashlight state
    var isFlashlightOn by remember { mutableStateOf(false) }

    // Keep screen awake state
    var isKeepAwakeOn by remember { mutableStateOf(false) }

    // Window Brightness state (0f to 1f)
    var currentBrightness by remember {
        val attrBrightness = activity?.window?.attributes?.screenBrightness ?: -1f
        mutableFloatStateOf(if (attrBrightness in 0f..1f) attrBrightness else 0.6f)
    }

    // Audio Profile / Ringer Mode
    var ringerMode by remember { mutableIntStateOf(audioManager.ringerMode) }

    // Volume states
    val maxMedia = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) }
    var mediaVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxMedia) }
    var prevMediaVolume by remember { mutableFloatStateOf(mediaVolume) }

    val maxRing = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_RING).coerceAtLeast(1) }
    var ringVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_RING).toFloat() / maxRing) }
    var prevRingVolume by remember { mutableFloatStateOf(ringVolume) }

    val maxNotif = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION).coerceAtLeast(1) }
    var notifVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION).toFloat() / maxNotif) }
    var prevNotifVolume by remember { mutableFloatStateOf(notifVolume) }

    val maxAlarm = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM).coerceAtLeast(1) }
    var alarmVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM).toFloat() / maxAlarm) }
    var prevAlarmVolume by remember { mutableFloatStateOf(alarmVolume) }

    // Cleanup flashlight and awake flag when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            if (isFlashlightOn && cameraManager != null) {
                try {
                    val camId = cameraManager.cameraIdList.firstOrNull()
                    if (camId != null) cameraManager.setTorchMode(camId, false)
                } catch (_: Exception) {}
            }
            if (isKeepAwakeOn) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle("Quick Control") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Quick Action Tiles (Grid)
            Text(
                text = "QUICK TILES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Flashlight Tile
                QuickTile(
                    modifier = Modifier.weight(1f),
                    title = "Flashlight",
                    subtitle = if (isFlashlightOn) "Turned ON" else "Turned OFF",
                    icon = if (isFlashlightOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                    isActive = isFlashlightOn,
                    onClick = {
                        val manager = cameraManager ?: return@QuickTile
                        try {
                            val camId = manager.cameraIdList.firstOrNull { id ->
                                manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                            } ?: manager.cameraIdList.firstOrNull() ?: return@QuickTile

                            val next = !isFlashlightOn
                            manager.setTorchMode(camId, next)
                            isFlashlightOn = next
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                // Keep Screen Awake Tile
                QuickTile(
                    modifier = Modifier.weight(1f),
                    title = "Stay Awake",
                    subtitle = if (isKeepAwakeOn) "Always On" else "Normal",
                    icon = Icons.Outlined.Visibility,
                    isActive = isKeepAwakeOn,
                    onClick = {
                        val next = !isKeepAwakeOn
                        isKeepAwakeOn = next
                        if (next) {
                            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wi-Fi / Connectivity Panel Shortcut
                QuickTile(
                    modifier = Modifier.weight(1f),
                    title = "Network",
                    subtitle = "Open Panel",
                    icon = Icons.Outlined.Wifi,
                    isActive = false,
                    onClick = {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            } else {
                                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                        } catch (_: Exception) {
                            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    }
                )

                // Developer Options Shortcut
                QuickTile(
                    modifier = Modifier.weight(1f),
                    title = "Dev Options",
                    subtitle = "USB & Debug",
                    icon = Icons.Outlined.DeveloperMode,
                    isActive = false,
                    onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        } catch (_: Exception) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Section 2: Display & Screen Brightness
            Text(
                text = "DISPLAY & BRIGHTNESS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (currentBrightness > 0.5f) Icons.Outlined.BrightnessHigh else Icons.Outlined.BrightnessLow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Screen Brightness",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${(currentBrightness * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = currentBrightness,
                        onValueChange = { newVal ->
                            currentBrightness = newVal
                            activity?.let { act ->
                                val lp = act.window.attributes
                                lp.screenBrightness = newVal.coerceIn(0.01f, 1.0f)
                                act.window.attributes = lp
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Section 3: Sound Profile & Ringer Mode
            Text(
                text = "SOUND PROFILE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = when (ringerMode) {
                                AudioManager.RINGER_MODE_NORMAL -> Icons.Outlined.Notifications
                                AudioManager.RINGER_MODE_VIBRATE -> Icons.Outlined.Vibration
                                else -> Icons.Outlined.NotificationsOff
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ringer Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (ringerMode) {
                                    AudioManager.RINGER_MODE_NORMAL -> "Ring & notifications enabled"
                                    AudioManager.RINGER_MODE_VIBRATE -> "Vibrate on calls & alerts"
                                    else -> "All sounds muted"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileChip(
                            title = "Normal",
                            icon = Icons.Outlined.Notifications,
                            isSelected = ringerMode == AudioManager.RINGER_MODE_NORMAL,
                            onClick = {
                                try {
                                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                    ringerMode = AudioManager.RINGER_MODE_NORMAL
                                } catch (_: Exception) {}
                            }
                        )
                        ProfileChip(
                            title = "Vibrate",
                            icon = Icons.Outlined.Vibration,
                            isSelected = ringerMode == AudioManager.RINGER_MODE_VIBRATE,
                            onClick = {
                                try {
                                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                    ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                } catch (_: Exception) {}
                            }
                        )
                        ProfileChip(
                            title = "Silent",
                            icon = Icons.Outlined.NotificationsOff,
                            isSelected = ringerMode == AudioManager.RINGER_MODE_SILENT,
                            onClick = {
                                try {
                                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                                    ringerMode = AudioManager.RINGER_MODE_SILENT
                                } catch (_: Exception) {}
                            }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Section 4: Granular Volume Controls with Mute/Restore
            Text(
                text = "VOLUME CHANNELS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Media
            EnhancedVolumeCard(
                title = "Media",
                value = mediaVolume,
                icon = Icons.AutoMirrored.Outlined.VolumeUp,
                onValueChange = { newVal ->
                    mediaVolume = newVal
                    val idx = (newVal * maxMedia).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, idx, 0)
                },
                onToggleMute = {
                    if (mediaVolume > 0f) {
                        prevMediaVolume = mediaVolume
                        mediaVolume = 0f
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    } else {
                        val restore = if (prevMediaVolume > 0.05f) prevMediaVolume else 0.5f
                        mediaVolume = restore
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (restore * maxMedia).toInt(), 0)
                    }
                }
            )

            // Ring / Calls
            EnhancedVolumeCard(
                title = "Ring & Calls",
                value = ringVolume,
                icon = Icons.Outlined.Phone,
                onValueChange = { newVal ->
                    ringVolume = newVal
                    val idx = (newVal * maxRing).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, idx, 0)
                },
                onToggleMute = {
                    if (ringVolume > 0f) {
                        prevRingVolume = ringVolume
                        ringVolume = 0f
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                    } else {
                        val restore = if (prevRingVolume > 0.05f) prevRingVolume else 0.5f
                        ringVolume = restore
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, (restore * maxRing).toInt(), 0)
                    }
                }
            )

            // Notifications
            EnhancedVolumeCard(
                title = "Notifications",
                value = notifVolume,
                icon = Icons.Outlined.Notifications,
                onValueChange = { newVal ->
                    notifVolume = newVal
                    val idx = (newVal * maxNotif).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, idx, 0)
                },
                onToggleMute = {
                    if (notifVolume > 0f) {
                        prevNotifVolume = notifVolume
                        notifVolume = 0f
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
                    } else {
                        val restore = if (prevNotifVolume > 0.05f) prevNotifVolume else 0.5f
                        notifVolume = restore
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, (restore * maxNotif).toInt(), 0)
                    }
                }
            )

            // Alarms
            EnhancedVolumeCard(
                title = "Alarm",
                value = alarmVolume,
                icon = Icons.Outlined.Alarm,
                onValueChange = { newVal ->
                    alarmVolume = newVal
                    val idx = (newVal * maxAlarm).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, idx, 0)
                },
                onToggleMute = {
                    if (alarmVolume > 0f) {
                        prevAlarmVolume = alarmVolume
                        alarmVolume = 0f
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0)
                    } else {
                        val restore = if (prevAlarmVolume > 0.05f) prevAlarmVolume else 0.5f
                        alarmVolume = restore
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (restore * maxAlarm).toInt(), 0)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected
        ),
        modifier = modifier
    )
}

@Composable
private fun EnhancedVolumeCard(
    title: String,
    value: Float,
    icon: ImageVector,
    onValueChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (value <= 0.01f) Icons.AutoMirrored.Outlined.VolumeMute else icon,
                        contentDescription = "Mute/Unmute",
                        tint = if (value <= 0.01f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${(value * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (value <= 0.01f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
