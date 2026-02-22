package com.carlosnc.doctordroid.modules.audio

import android.content.Context
import android.media.AudioManager
import android.os.Build

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

fun getAudioInfo(context: Context): com.carlosnc.doctordroid.modules.audio.AudioInfo {
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
