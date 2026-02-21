package com.carlosnc.doctordroid.ui.components.camera

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.PageTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraInfoList = remember { getCameraDetails(context) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
               title = { PageTitle("Camera Information") },
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
                .padding(bottom = 16.dp)
        ) {
            CameraHeader(cameraCount = cameraInfoList.size)

            cameraInfoList.forEachIndexed { index, cameraInfo ->
                InfoSection(title = "Camera ${cameraInfo.id} (${cameraInfo.facing})")
                
                CameraDetailItem(
                    label = "Resolution",
                    value = cameraInfo.resolution,
                    icon = if (cameraInfo.facing == "Back") Icons.Default.CameraRear else Icons.Default.CameraFront
                )
                CameraDetailItem(
                    label = "MegaPixels",
                    value = cameraInfo.megaPixels,
                    icon = Icons.Default.CameraAlt
                )
                CameraDetailItem(
                    label = "Flash Support",
                    value = cameraInfo.hasFlash,
                    icon = Icons.Default.FlashOn
                )
                CameraDetailItem(
                    label = "Sensor Size",
                    value = cameraInfo.sensorSize,
                    icon = Icons.Default.Settings
                )
                CameraDetailItem(
                    label = "Video Support",
                    value = cameraInfo.videoCapabilities,
                    icon = Icons.Default.Videocam
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CameraHeader(cameraCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$cameraCount Cameras Detected",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Total lenses on this device",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CameraDetailItem(label: String, value: String, icon: ImageVector) {
    ListItem(
        headlineContent = { 
            Text(
                text = label, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            ) 
        },
        supportingContent = { 
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@Composable
fun InfoSection(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
    )
}

data class CameraInfo(
    val id: String,
    val facing: String,
    val resolution: String,
    val megaPixels: String,
    val hasFlash: String,
    val sensorSize: String,
    val videoCapabilities: String
)

fun getCameraDetails(context: Context): List<CameraInfo> {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val result = mutableListOf<CameraInfo>()

    try {
        for (cameraId in cameraManager.cameraIdList) {
            val chars = cameraManager.getCameraCharacteristics(cameraId)
            
            val facing = when (chars.get(CameraCharacteristics.LENS_FACING)) {
                CameraCharacteristics.LENS_FACING_BACK -> "Back"
                CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
                else -> "Unknown"
            }

            val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val sizes = map?.getOutputSizes(ImageFormat.JPEG)
            val largestSize = sizes?.maxByOrNull { it.width * it.height }
            
            val resolution = if (largestSize != null) "${largestSize.width} x ${largestSize.height}" else "Unknown"
            val mp = if (largestSize != null) {
                val megapixels = (largestSize.width * largestSize.height).toFloat() / 1_000_000
                "%.1f MP".format(megapixels)
            } else "Unknown"

            val flash = if (chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) "Supported" else "Not Supported"
            
            val sensorArea = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
            val sensorSize = if (sensorArea != null) "${sensorArea.width} x ${sensorArea.height} mm" else "Unknown"

            val videoCapabilities = if (map?.getOutputSizes(android.graphics.SurfaceTexture::class.java)?.any { it.width >= 1920 } == true) {
                "Full HD (1080p) supported"
            } else {
                "Standard HD supported"
            }

            result.add(
                CameraInfo(
                    id = cameraId,
                    facing = facing,
                    resolution = resolution,
                    megaPixels = mp,
                    hasFlash = flash,
                    sensorSize = sensorSize,
                    videoCapabilities = videoCapabilities
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result
}
