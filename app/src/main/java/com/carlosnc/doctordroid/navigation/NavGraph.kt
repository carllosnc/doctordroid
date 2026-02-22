package com.carlosnc.doctordroid.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carlosnc.doctordroid.modules.audio.AudioScreen
import com.carlosnc.doctordroid.modules.battery.BatteryScreen
import com.carlosnc.doctordroid.modules.camera.CameraScreen
import com.carlosnc.doctordroid.modules.cpu.CpuScreen
import com.carlosnc.doctordroid.modules.device.DeviceScreen
import com.carlosnc.doctordroid.modules.gpu.GpuScreen
import com.carlosnc.doctordroid.modules.health.HealthCheckScreen
import com.carlosnc.doctordroid.modules.memory.MemoryScreen
import com.carlosnc.doctordroid.modules.network.NetworkScreen
import com.carlosnc.doctordroid.modules.storage.StorageScreen
import com.carlosnc.doctordroid.modules.temperature.TemperatureScreen
import com.carlosnc.doctordroid.screens.DeviceResumeScreen
import com.carlosnc.doctordroid.screens.HomeScreen
import com.carlosnc.doctordroid.screens.QuickControlScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Storage

@Serializable
object Memory

@Serializable
object Battery

@Serializable
object Cpu

@Serializable
object Gpu

@Serializable
object Camera

@Serializable
object Device

@Serializable
object Temperature

@Serializable
object Network

@Serializable
object Audio

@Serializable
object QuickControl

@Serializable
object HealthCheck

@Serializable
object DeviceResume

@Composable
fun AppNavHost(
    navController: NavHostController,
    isMonitorActive: Boolean,
    onToggleFloatingMonitor: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        }
    ) {
        composable<Home> {
            HomeScreen(
                isMonitorActive = isMonitorActive,
                onStorageClick = { navController.navigate(Storage) },
                onMemoryClick = { navController.navigate(Memory) },
                onBatteryClick = { navController.navigate(Battery) },
                onCpuClick = { navController.navigate(Cpu) },
                onGpuClick = { navController.navigate(Gpu) },
                onCameraClick = { navController.navigate(Camera) },
                onDeviceClick = { navController.navigate(Device) },
                onTemperatureClick = { navController.navigate(Temperature) },
                onNetworkClick = { navController.navigate(Network) },
                onAudioClick = { navController.navigate(Audio) },
                onQuickControlClick = { navController.navigate(QuickControl) },
                onHealthCheckClick = { navController.navigate(HealthCheck) },
                onDeviceResumeClick = { navController.navigate(DeviceResume) },
                onToggleFloatingMonitor = onToggleFloatingMonitor
            )
        }
        composable<QuickControl> {
            QuickControlScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Storage> {
            StorageScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Memory> {
            MemoryScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Battery> {
            BatteryScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Cpu> {
            CpuScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Gpu> {
            GpuScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Camera> {
            CameraScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Device> {
            DeviceScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Temperature> {
            TemperatureScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Network> {
            NetworkScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Audio> {
            AudioScreen(onBackClick = { navController.popBackStack() })
        }
        composable<HealthCheck> {
            HealthCheckScreen(onBackClick = { navController.popBackStack() })
        }
        composable<DeviceResume> {
            DeviceResumeScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
