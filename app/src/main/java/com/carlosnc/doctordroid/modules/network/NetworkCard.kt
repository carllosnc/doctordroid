package com.carlosnc.doctordroid.modules.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun NetworkCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val networkStatus = getNetworkStatus(context)
    
    DashboardListItem(
        title = "Network & Connectivity",
        subtitle = networkStatus,
        leftIcon = Icons.Default.Wifi,
        onClick = onClick,
        modifier = modifier
    )
}

private fun getNetworkStatus(context: Context): String {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return "Disconnected"
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "Disconnected"
    
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi Connected"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Connected"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet Connected"
        else -> "Connected"
    }
}
