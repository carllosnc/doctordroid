package com.carlosnc.doctordroid.ui.components.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var networkInfo by remember { mutableStateOf(getNetworkDetails(context)) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            networkInfo = getNetworkDetails(context)
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Network & Connectivity") },
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
            InfoSection(title = "Connection")
            ConnectionInfoContent(networkInfo)
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            InfoSection(title = "WiFi Details")
            WifiInfoContent(networkInfo)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "IP Addresses")
            IpInfoContent(networkInfo)
            
            Box(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ConnectionInfoContent(networkInfo: NetworkDetails) {
    Column {
        InfoItem(label = "Status", value = networkInfo.status, icon = Icons.Default.NetworkCheck)
        InfoItem(label = "Type", value = networkInfo.type, icon = Icons.Default.AltRoute)
        InfoItem(label = "Data State", value = networkInfo.dataState, icon = Icons.Default.Public)
        InfoItem(label = "Roaming", value = networkInfo.roaming, icon = Icons.Default.SettingsInputAntenna)
    }
}

@Composable
fun WifiInfoContent(networkInfo: NetworkDetails) {
    Column {
        InfoItem(label = "SSID", value = networkInfo.wifiSsid, icon = Icons.Default.Wifi)
        InfoItem(label = "BSSID", value = networkInfo.wifiBssid, icon = Icons.Default.Router)
        InfoItem(label = "Frequency", value = networkInfo.wifiFreq, icon = Icons.Default.WifiTethering)
        InfoItem(label = "Link Speed", value = networkInfo.wifiLinkSpeed, icon = Icons.Default.NetworkCheck)
    }
}

@Composable
fun IpInfoContent(networkInfo: NetworkDetails) {
    Column {
        InfoItem(label = "IPv4 Address", value = networkInfo.ipv4, icon = Icons.Default.Dns)
        InfoItem(label = "IPv6 Address", value = networkInfo.ipv6, icon = Icons.Default.Public)
        InfoItem(label = "MAC Address", value = networkInfo.macAddress, icon = Icons.Default.Bluetooth)
    }
}

@Composable
private fun InfoSection(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun InfoItem(label: String, value: String, icon: ImageVector) {
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
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
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

data class NetworkDetails(
    val status: String,
    val type: String,
    val dataState: String,
    val roaming: String,
    val wifiSsid: String,
    val wifiBssid: String,
    val wifiFreq: String,
    val wifiLinkSpeed: String,
    val ipv4: String,
    val ipv6: String,
    val macAddress: String
)

fun getNetworkDetails(context: Context): NetworkDetails {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    val activeNetwork = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(activeNetwork)
    
    val status = if (activeNetwork != null) "Connected" else "Disconnected"
    val type = when {
        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile Data"
        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "Bluetooth"
        else -> "None"
    }

    val dataState = when (tm.dataState) {
        TelephonyManager.DATA_CONNECTED -> "Connected"
        TelephonyManager.DATA_CONNECTING -> "Connecting"
        TelephonyManager.DATA_DISCONNECTED -> "Disconnected"
        TelephonyManager.DATA_SUSPENDED -> "Suspended"
        else -> "Unknown"
    }

    val roaming = if (tm.isNetworkRoaming) "Yes" else "No"

    var ssid = "N/A"
    var bssid = "N/A"
    var freq = "N/A"
    var linkSpeed = "N/A"

    try {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            val wifiInfo = wm.connectionInfo
            if (wifiInfo != null) {
                ssid = if (wifiInfo.networkId != -1) wifiInfo.ssid.removeSurrounding("\"") else "N/A"
                bssid = wifiInfo.bssid ?: "N/A"
                freq = "${wifiInfo.frequency} MHz"
                linkSpeed = "${wifiInfo.linkSpeed} Mbps"
            }
        }
    } catch (e: SecurityException) {
        ssid = "Permission Denied"
    }

    var ipv4 = "N/A"
    var ipv6 = "N/A"
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress ?: continue
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (isIPv4) {
                        ipv4 = sAddr
                    } else {
                        val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                        ipv6 = if (delim < 0) sAddr.uppercase() else sAddr.substring(0, delim).uppercase()
                    }
                }
            }
        }
    } catch (e: Exception) {}

    val macAddress = try {
        val all = Collections.list(NetworkInterface.getNetworkInterfaces())
        var mac = "N/A"
        for (nif in all) {
            if (nif.name.equals("wlan0", ignoreCase = true)) {
                val res = nif.hardwareAddress
                if (res != null) {
                    val sb = StringBuilder()
                    for (b in res) {
                        sb.append(String.format("%02X:", b))
                    }
                    if (sb.isNotEmpty()) {
                        sb.deleteCharAt(sb.length - 1)
                    }
                    mac = sb.toString()
                }
            }
        }
        mac
    } catch (e: Exception) { "N/A" }

    return NetworkDetails(
        status = status,
        type = type,
        dataState = dataState,
        roaming = roaming,
        wifiSsid = ssid,
        wifiBssid = bssid,
        wifiFreq = freq,
        wifiLinkSpeed = linkSpeed,
        ipv4 = ipv4,
        ipv6 = ipv6,
        macAddress = macAddress
    )
}
