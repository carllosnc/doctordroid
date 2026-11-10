package com.carlosnc.doctordroid.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.display.DisplayManager
import android.os.Build
import android.telephony.TelephonyManager
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Battery0Bar
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.carlosnc.doctordroid.modules.battery.getBatteryInfo
import com.carlosnc.doctordroid.modules.device.rememberDeviceInfo
import com.carlosnc.doctordroid.modules.memory.getMemoryInfo
import com.carlosnc.doctordroid.modules.network.getNetworkDetails
import com.carlosnc.doctordroid.modules.storage.getStorageInfo
import com.carlosnc.doctordroid.ui.components.PageTitle
import com.carlosnc.doctordroid.utils.formatSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceResumeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceResumeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val brand = uiState.brand
    val model = uiState.model
    val coreCount = uiState.coreCount
    val processorName = uiState.processorName
    val securityPatch = uiState.securityPatch
    val formattedDate = uiState.formattedDate
    val storageInfo = uiState.storageInfo
    val memoryInfo = uiState.memoryInfo
    val batteryInfo = uiState.batteryInfo
    val networkInfo = uiState.networkInfo
    val simInfo = uiState.simInfo
    val displayInfo = uiState.displayInfo
    val healthScore = uiState.healthScore
    val healthRating = uiState.healthRating
    val formattedSpecSheet = uiState.formattedSpecSheet

    val storageUsedPct = if (storageInfo.totalBytes > 0) {
        (storageInfo.usedBytes.toFloat() / storageInfo.totalBytes.toFloat()) * 100f
    } else 0f
    val batteryTempC = batteryInfo.temperature / 10f

    val scoreColor = when {
        healthScore >= 90 -> Color(0xFF10B981) // Emerald Green
        healthScore >= 80 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFEF4444)              // Red
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PageTitle("Ficha do Aparelho") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareResumeImage(context, view, scope)
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Compartilhar Imagem")
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Device Header (Clean hierarchy: Hero device name + Report subtitle)
            val displayName = formatDeviceDisplayName(brand, model)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Emitido em $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2. Harmonious Status Banner (Unified Certification & Health Score)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = scoreColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(scoreColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = scoreColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = healthRating,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "DoctorDroid Certified",
                            style = MaterialTheme.typography.bodySmall,
                            color = scoreColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = scoreColor
                    ) {
                        Text(
                            text = "$healthScore pts",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 4 Quick Highlight Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HighlightMiniCard(
                    icon = Icons.Outlined.Memory,
                    label = "RAM",
                    value = formatMemorySize(memoryInfo.totalMem),
                    subValue = "${formatMemorySize(memoryInfo.availMem)} livre",
                    modifier = Modifier.weight(1f)
                )
                HighlightMiniCard(
                    icon = Icons.Outlined.SdStorage,
                    label = "Espaço",
                    value = formatSize(storageInfo.totalBytes),
                    subValue = "${(100 - storageUsedPct.toInt())}% livre",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HighlightMiniCard(
                    icon = Icons.Outlined.Battery0Bar,
                    label = "Bateria",
                    value = "${batteryInfo.level}%",
                    subValue = "${batteryInfo.health} • ${batteryTempC}°C",
                    modifier = Modifier.weight(1f)
                )
                HighlightMiniCard(
                    icon = Icons.Outlined.Speed,
                    label = "Processador",
                    value = "$coreCount Cores",
                    subValue = processorName,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Detailed Technical Sections
            ResumeSection(title = "Identificação", icon = Icons.Outlined.PhoneAndroid) {
                ResumeItem("Fabricante", brand)
                ResumeItem("Modelo", model)
                ResumeItem("Dispositivo", Build.DEVICE)
                ResumeItem("Placa", Build.BOARD)
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            ResumeSection(title = "Tela & Display", icon = Icons.Outlined.DisplaySettings) {
                ResumeItem("Resolução Nativa", displayInfo.first)
                ResumeItem("Taxa de Atualização", displayInfo.second)
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            ResumeSection(title = "Conectividade", icon = Icons.Outlined.Wifi) {
                ResumeItem("Tipo de Rede", networkInfo.type)
                if (networkInfo.type == "WiFi") {
                    ResumeItem("Padrão Wi-Fi", networkInfo.wifiStandard)
                }
                ResumeItem("Configuração SIM", simInfo)
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            ResumeSection(title = "Energia & Bateria", icon = Icons.Outlined.Battery0Bar) {
                ResumeItem("Nível de Carga", "${batteryInfo.level}%")
                ResumeItem("Status", batteryInfo.status)
                ResumeItem("Saúde da Bateria", batteryInfo.health)
                ResumeItem("Temperatura", "${batteryTempC}°C")
                ResumeItem("Tecnologia", batteryInfo.technology)
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            ResumeSection(title = "Sistema & Segurança", icon = Icons.Outlined.Security) {
                ResumeItem("Versão do Android", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                ResumeItem("Patch de Segurança", securityPatch)
                ResumeItem("Arquitetura ABI", Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = ClipData.newPlainText("DoctorDroid Spec Sheet", formattedSpecSheet)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, "Ficha copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar Ficha", fontWeight = FontWeight.Bold, maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, formattedSpecSheet)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartilhar Ficha Técnica"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartilhar", fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HighlightMiniCard(
    icon: ImageVector,
    label: String,
    value: String,
    subValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun calculateHealthScore(
    batteryHealth: String,
    batteryTemp: Float,
    storageUsedPct: Float,
    memoryUsedPct: Float
): Pair<Int, String> {
    var score = 100

    if (batteryHealth != "Good") score -= 15
    if (batteryTemp > 42f) score -= 10
    else if (batteryTemp > 38f) score -= 5

    if (storageUsedPct > 95f) score -= 20
    else if (storageUsedPct > 85f) score -= 10

    if (memoryUsedPct > 92f) score -= 10

    val finalScore = score.coerceIn(50, 100)
    val rating = when {
        finalScore >= 90 -> "Excelente (A+)"
        finalScore >= 80 -> "Bom (B)"
        finalScore >= 70 -> "Regular (C)"
        else -> "Atenção Requerida"
    }
    return Pair(finalScore, rating)
}

fun getDisplayInfo(context: Context): Pair<String, String> {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
    val display: Display? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay
        }
    } catch (_: Throwable) {
        displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
    }
    val refreshRate = display?.refreshRate?.toInt() ?: 60
    val metrics = context.resources.displayMetrics
    val resolution = "${metrics.widthPixels} x ${metrics.heightPixels}"
    return Pair(resolution, "${refreshRate}Hz")
}

fun getSimInfo(context: Context): String {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return "Unknown"
    val simCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        telephonyManager.supportedModemCount
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        telephonyManager.phoneCount
    } else {
        1
    }
    return if (simCount > 1) "Dual SIM" else "Single SIM"
}

@Composable
fun ResumeSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

@Composable
fun ResumeItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
    }
}

fun formatMemorySize(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format(Locale.getDefault(), "%.1f GB", gb)
}

fun formatDeviceDisplayName(brand: String, model: String): String {
    val cleanModel = if (model.startsWith(brand, ignoreCase = true)) {
        model.substring(brand.length).trim()
    } else {
        model
    }
    val full = if (cleanModel.isNotBlank()) "$brand $cleanModel" else model
    return full.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

fun shareResumeImage(context: Context, view: View, scope: CoroutineScope) {
    if (view.width <= 0 || view.height <= 0) return
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    scope.launch(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, "device_resume.png")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Ficha Técnica"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}