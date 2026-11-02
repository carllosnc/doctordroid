package com.carlosnc.doctordroid.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.carlosnc.doctordroid.modules.battery.getBatteryTemperature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

class FloatingMonitorService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    companion object {
        var isRunning = false
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        startForegroundService()
        showFloatingWindow()
    }

    private fun startForegroundService() {
        val channelId = "floating_monitor_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Floating Monitor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Floating Monitor Active")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun showFloatingWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.TOP or Gravity.START
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            x = 100
            y = 100
        }

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingMonitorService)
            setViewTreeViewModelStoreOwner(this@FloatingMonitorService)
            setViewTreeSavedStateRegistryOwner(this@FloatingMonitorService)
            setContent {
                FloatingMonitorContent(
                    onDrag = { dx, dy ->
                        layoutParams.x += dx.toInt()
                        layoutParams.y += dy.toInt()
                        try {
                            windowManager.updateViewLayout(this, layoutParams)
                        } catch (_: Exception) {}
                    }
                )
            }
        }

        floatingView = composeView
        try {
            windowManager.addView(floatingView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Composable
    private fun FloatingMonitorContent(onDrag: (Float, Float) -> Unit) {
        var fps by remember { mutableIntStateOf(0) }
        var temperature by remember { mutableFloatStateOf(0f) }

        // Update Temperature
        LaunchedEffect(Unit) {
            while (isActive) {
                temperature = getBatteryTemperature(this@FloatingMonitorService)
                delay(5000)
            }
        }

        // Update FPS
        val choreographer = remember { Choreographer.getInstance() }
        DisposableEffect(Unit) {
            var frameCount = 0
            var startTime = System.nanoTime()
            val callback = object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    frameCount++
                    val currentTime = System.nanoTime()
                    if (currentTime - startTime >= 1_000_000_000L) {
                        fps = frameCount
                        frameCount = 0
                        startTime = currentTime
                    }
                    try {
                        choreographer.postFrameCallback(this)
                    } catch (_: Exception) {
                        // Choreographer may be disposed
                    }
                }
            }
            choreographer.postFrameCallback(callback)
            onDispose {
                choreographer.removeFrameCallback(callback)
            }
        }

        Surface(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                },
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                MonitorText("FPS: $fps")
                MonitorText(String.format(Locale.getDefault(), "TEMP: %.1f°C", temperature))
            }
        }
    }

    @Composable
    private fun MonitorText(text: String) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        serviceJob.cancel()
        store.clear()
        floatingView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {}
        }
        floatingView = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
