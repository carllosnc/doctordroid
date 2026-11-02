package com.carlosnc.doctordroid.modules.health

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@SuppressLint("MissingPermission")
@Composable
fun HardwareTestList(
    onShowTouchTest: () -> Unit,
    onShowDeadPixelTest: () -> Unit
) {
    val context = LocalContext.current
    val hardwareTester = remember { HardwareTester(context.applicationContext) }

    DisposableEffect(hardwareTester) {
        onDispose {
            hardwareTester.cleanup()
        }
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        DashboardListItem(
            title = "Touch Screen Test", 
            subtitle = "Test multi-touch and detect dead zones", 
            leftIcon = Icons.Outlined.TouchApp,
            rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            onClick = onShowTouchTest
        )
        DashboardListItem(
            title = "Dead Pixel & Burn-in", 
            subtitle = "Full-screen RGB subpixel check", 
            leftIcon = Icons.Outlined.BrightnessMedium,
            rightIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            onClick = onShowDeadPixelTest
        )
        DashboardListItem(
            title = "Audio Output", 
            subtitle = "Play a high-frequency acoustic test tone", 
            leftIcon = Icons.AutoMirrored.Outlined.VolumeUp,
            rightIcon = Icons.Outlined.PlayArrow,
            onClick = { hardwareTester.playTestTone() }
        )
        DashboardListItem(
            title = "Flashlight", 
            subtitle = if (hardwareTester.isFlashOn) "Torch is currently ON" else "Torch is currently OFF", 
            leftIcon = if (hardwareTester.isFlashOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
            trailingContent = {
                Switch(
                    checked = hardwareTester.isFlashOn,
                    onCheckedChange = { hardwareTester.toggleFlashlight() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        checkedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            },
            onClick = { hardwareTester.toggleFlashlight() }
        )
        DashboardListItem(
            title = "Vibration Motor", 
            subtitle = "Tap to trigger haptic feedback pulse", 
            leftIcon = Icons.Outlined.Vibration,
            rightIcon = Icons.Outlined.PlayArrow,
            onClick = { hardwareTester.vibrateDevice() }
        )
    }
}

class HardwareTester(private val context: Context) {
    var isFlashOn by mutableStateOf(false)
        private set
    private var toneGen: ToneGenerator? = null
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    fun playTestTone() {
        try {
            toneGen?.release()
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleFlashlight() {
        val manager = cameraManager ?: return
        try {
            val cameraId = manager.cameraIdList.firstOrNull { id ->
                val chars = manager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: manager.cameraIdList.firstOrNull() ?: return

            val newState = !isFlashOn
            manager.setTorchMode(cameraId, newState)
            isFlashOn = newState
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    fun vibrateDevice() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(300)
        }
    }

    fun cleanup() {
        if (isFlashOn) {
            try {
                cameraManager?.cameraIdList?.firstOrNull()?.let { id ->
                    cameraManager.setTorchMode(id, false)
                }
            } catch (_: Exception) {}
            isFlashOn = false
        }
        toneGen?.release()
        toneGen = null
    }
}
