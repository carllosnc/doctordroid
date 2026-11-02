package com.carlosnc.doctordroid.screens

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.carlosnc.doctordroid.modules.battery.BatteryInfo
import com.carlosnc.doctordroid.modules.battery.getBatteryInfo
import com.carlosnc.doctordroid.modules.memory.MemoryInfo
import com.carlosnc.doctordroid.modules.memory.getMemoryInfo
import com.carlosnc.doctordroid.modules.network.NetworkDetails
import com.carlosnc.doctordroid.modules.network.getNetworkDetails
import com.carlosnc.doctordroid.modules.storage.StorageInfo
import com.carlosnc.doctordroid.modules.storage.getStorageInfo
import com.carlosnc.doctordroid.utils.formatSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DeviceResumeUiState(
    val brand: String,
    val model: String,
    val coreCount: Int,
    val processorName: String,
    val securityPatch: String,
    val formattedDate: String,
    val storageInfo: StorageInfo,
    val memoryInfo: MemoryInfo,
    val batteryInfo: BatteryInfo,
    val networkInfo: NetworkDetails,
    val simInfo: String,
    val displayInfo: Pair<String, String>,
    val healthScore: Int,
    val healthRating: String,
    val formattedSpecSheet: String
)

class DeviceResumeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(createUiState(application))
    val uiState: StateFlow<DeviceResumeUiState> = _uiState.asStateFlow()

    fun refresh() {
        _uiState.value = createUiState(getApplication())
    }

    private fun createUiState(context: Application): DeviceResumeUiState {
        val storage = getStorageInfo()
        val memory = getMemoryInfo(context)
        val battery = getBatteryInfo(context)
        val network = getNetworkDetails(context)
        val sim = getSimInfo(context)
        val display = getDisplayInfo(context)

        val brand = Build.BRAND.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val coreCount = Runtime.getRuntime().availableProcessors()
        val processor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL.takeIf { it.isNotBlank() && it != Build.UNKNOWN } ?: Build.HARDWARE
        } else {
            Build.HARDWARE
        }
        val secPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A"
        val dateStr = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()).format(Date())

        val storageUsedPct = if (storage.totalBytes > 0) {
            (storage.usedBytes.toFloat() / storage.totalBytes.toFloat()) * 100f
        } else 0f
        val memUsedBytes = (memory.totalMem - memory.availMem).coerceAtLeast(0L)
        val memUsedPct = if (memory.totalMem > 0) {
            (memUsedBytes.toFloat() / memory.totalMem.toFloat()) * 100f
        } else 0f
        val batteryTempC = battery.temperature / 10f

        val (score, rating) = calculateHealthScore(
            batteryHealth = battery.health,
            batteryTemp = batteryTempC,
            storageUsedPct = storageUsedPct,
            memoryUsedPct = memUsedPct
        )

        val specSheet = buildString {
            appendLine("📱 FICHA TÉCNICA - ${brand.uppercase()} $model")
            appendLine("⭐ Diagnóstico DoctorDroid: $score/100 ($rating)")
            appendLine("📅 Emitido em: $dateStr")
            appendLine()
            appendLine("⚡ Processador: $processor ($coreCount Cores, ${Build.SUPPORTED_ABIS.firstOrNull() ?: "ARM64"})")
            appendLine("🖥️ Tela: ${display.first} @ ${display.second}")
            appendLine("🧠 Memória RAM: ${formatMemorySize(memory.totalMem)} (${formatMemorySize(memory.availMem)} livre)")
            appendLine("💾 Armazenamento: ${formatSize(storage.totalBytes)} (${formatSize(storage.totalBytes - storage.usedBytes)} livre)")
            appendLine("🔋 Bateria: ${battery.level}% • Saúde ${battery.health} (${batteryTempC}°C)")
            appendLine("📶 Rede: ${network.type}${if (network.type == "WiFi") " (${network.wifiStandard})" else ""} • $sim")
            appendLine("🤖 Sistema: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("🛡️ Patch de Segurança: $secPatch")
            appendLine()
            appendLine("Verificado com DoctorDroid")
        }

        return DeviceResumeUiState(
            brand = brand,
            model = model,
            coreCount = coreCount,
            processorName = processor,
            securityPatch = secPatch,
            formattedDate = dateStr,
            storageInfo = storage,
            memoryInfo = memory,
            batteryInfo = battery,
            networkInfo = network,
            simInfo = sim,
            displayInfo = display,
            healthScore = score,
            healthRating = rating,
            formattedSpecSheet = specSheet
        )
    }
}
