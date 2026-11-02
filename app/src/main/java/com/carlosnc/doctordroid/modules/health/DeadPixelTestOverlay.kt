package com.carlosnc.doctordroid.modules.health

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val testColors = listOf(
    Color(0xFFFF0000) to "Red (Primary Subpixel)",
    Color(0xFF00FF00) to "Green (Primary Subpixel)",
    Color(0xFF0000FF) to "Blue (Primary Subpixel)",
    Color(0xFFFFFFFF) to "Pure White (All Subpixels)",
    Color(0xFF000000) to "Pure Black (OLED True Black)",
    Color(0xFFFFFF00) to "Yellow",
    Color(0xFFFF00FF) to "Magenta",
    Color(0xFF00FFFF) to "Cyan"
)

@Composable
fun DeadPixelTestOverlay(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var colorIndex by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(true) }

    // Intercept back button to close test
    BackHandler {
        onDismiss()
    }

    // Boost window brightness to maximum during test to reveal dead subpixels & burn-in
    DisposableEffect(Unit) {
        val originalBrightness = activity?.window?.attributes?.screenBrightness ?: -1f
        activity?.let { act ->
            val lp = act.window.attributes
            lp.screenBrightness = 1.0f
            act.window.attributes = lp
        }

        onDispose {
            activity?.let { act ->
                val lp = act.window.attributes
                lp.screenBrightness = originalBrightness
                act.window.attributes = lp
            }
        }
    }

    // Auto-hide hint after 3 seconds
    LaunchedEffect(Unit) {
        delay(3200)
        showHint = false
    }

    val currentColorPair = testColors[colorIndex]
    val currentColor = currentColorPair.first
    val colorLabel = currentColorPair.second

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (colorIndex < testColors.size - 1) {
                    colorIndex++
                } else {
                    onDismiss()
                }
            }
    ) {
        // Overlay hint card
        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Dead Pixel & Burn-in Test",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap anywhere to cycle colors.\nLook closely for stuck, black, or discolored dots.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showHint = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Start Checking", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Small unobtrusive bottom banner with progress and exit
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            color = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "${colorIndex + 1}/${testColors.size}: $colorLabel • Tap to advance",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
