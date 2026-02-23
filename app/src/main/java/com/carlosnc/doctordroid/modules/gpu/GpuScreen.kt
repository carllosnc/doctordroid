package com.carlosnc.doctordroid.modules.gpu

import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.carlosnc.doctordroid.ui.components.DashboardListItem
import com.carlosnc.doctordroid.ui.components.PageTitle
import kotlinx.coroutines.delay
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpuScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var gpuInfo by remember { mutableStateOf<GpuInfo?>(null) }
    var showExtractor by remember { mutableStateOf(false) }

    // Wait for navigation transition to finish to avoid flickering
    LaunchedEffect(Unit) {
        delay(600)
        showExtractor = true
    }

    // Using a hidden, delayed GLSurfaceView to extract GPU info
    if (showExtractor && gpuInfo == null) {
        Box(modifier = Modifier.size(1.dp).alpha(0f)) {
            AndroidView(
                factory = { ctx ->
                    GLSurfaceView(ctx).apply {
                        // Make surface transparent to avoid black flicker
                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                        holder.setFormat(PixelFormat.TRANSLUCENT)
                        setZOrderOnTop(true)
                        
                        setEGLContextClientVersion(2)
                        setRenderer(object : GLSurfaceView.Renderer {
                            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                                val renderer = GLES20.glGetString(GLES20.GL_RENDERER)
                                val vendor = GLES20.glGetString(GLES20.GL_VENDOR)
                                val version = GLES20.glGetString(GLES20.GL_VERSION)
                                val extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)
                                
                                gpuInfo = GpuInfo(
                                    renderer = renderer ?: "Unknown",
                                    vendor = vendor ?: "Unknown",
                                    version = version ?: "Unknown",
                                    extensions = extensions ?: ""
                                )
                            }
                            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {}
                            override fun onDrawFrame(gl: GL10?) {}
                        })
                        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                    }
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle(text = "GPU & Graphics") },
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
            GpuHeaderCard(gpuInfo)

            if (gpuInfo == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                InfoSection(title = "Graphics Engine")
                GpuDetailItem(
                    label = "OpenGL Version",
                    value = gpuInfo!!.version,
                    icon = Icons.Default.Layers
                )
                GpuDetailItem(
                    label = "Vendor",
                    value = gpuInfo!!.vendor,
                    icon = Icons.Default.Settings
                )
                
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val configInfo = activityManager.deviceConfigurationInfo
                GpuDetailItem(
                    label = "GLES Version",
                    value = configInfo.glEsVersion,
                    icon = Icons.Default.DeveloperMode
                )

                InfoSection(title = "Capabilities")
                val extensionCount = gpuInfo!!.extensions.split(" ").filter { it.isNotBlank() }.size
                GpuDetailItem(
                    label = "GL Extensions",
                    value = "$extensionCount supported",
                    icon = Icons.Default.ViewInAr
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GpuHeaderCard(gpuInfo: GpuInfo?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = gpuInfo?.renderer ?: "Detecting GPU...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Graphics Processing Unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GpuDetailItem(label: String, value: String, icon: ImageVector) {
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

data class GpuInfo(
    val renderer: String,
    val vendor: String,
    val version: String,
    val extensions: String
)
