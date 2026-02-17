package com.carlosnc.doctordroid.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carlosnc.doctordroid.ui.components.battery.BatteryScreen
import com.carlosnc.doctordroid.ui.components.cpu.CpuScreen
import com.carlosnc.doctordroid.ui.components.device.DeviceScreen
import com.carlosnc.doctordroid.ui.components.memory.MemoryScreen
import com.carlosnc.doctordroid.ui.components.storage.StorageScreen
import com.carlosnc.doctordroid.ui.screens.HomeScreen
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
object Device

@Composable
fun AppNavHost(
    navController: NavHostController,
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
                onStorageClick = { navController.navigate(Storage) },
                onMemoryClick = { navController.navigate(Memory) },
                onBatteryClick = { navController.navigate(Battery) },
                onCpuClick = { navController.navigate(Cpu) },
                onDeviceClick = { navController.navigate(Device) }
            )
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
        composable<Device> {
            DeviceScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
