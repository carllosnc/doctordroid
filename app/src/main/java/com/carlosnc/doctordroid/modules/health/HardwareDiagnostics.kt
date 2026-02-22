package com.carlosnc.doctordroid.modules.health

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@SuppressLint("MissingPermission")
@Composable
fun HardwareTestList(onShowTouchTest: () -> Unit) {
    val context = LocalContext.current
    
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        DashboardListItem(
            title = "Touch Screen Test", 
            subtitle = "Check multi-touch and dead zones", 
            leftIcon = Icons.Default.TouchApp,
            onClick = onShowTouchTest,
            trailingContent = { TestButton(onClick = onShowTouchTest) }
        )
        DashboardListItem(
            title = "Audio Output", 
            subtitle = "Play a test tone", 
            leftIcon = Icons.AutoMirrored.Filled.VolumeUp,
            onClick = { playTestTone() },
            trailingContent = { TestButton(onClick = { playTestTone() }) }
        )
        DashboardListItem(
            title = "Flashlight Test", 
            subtitle = "Toggle device flashlight", 
            leftIcon = Icons.Default.FlashOn,
            onClick = { toggleFlashlight(context) },
            trailingContent = { TestButton(onClick = { toggleFlashlight(context) }) }
        )
        DashboardListItem(
            title = "Vibration Motor", 
            subtitle = "Verify haptic feedback", 
            leftIcon = Icons.Default.Vibration,
            onClick = { vibrateDevice(context) },
            trailingContent = { TestButton(onClick = { vibrateDevice(context) }) }
        )
    }
}

@Composable
private fun TestButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Test", fontSize = 12.sp)
    }
}

private fun playTestTone() {
    try {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private var isFlashOn = false
private fun toggleFlashlight(context: Context) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    try {
        val cameraId = cameraManager.cameraIdList[0]
        isFlashOn = !isFlashOn
        cameraManager.setTorchMode(cameraId, isFlashOn)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@SuppressLint("MissingPermission")
private fun vibrateDevice(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(300)
    }
}
