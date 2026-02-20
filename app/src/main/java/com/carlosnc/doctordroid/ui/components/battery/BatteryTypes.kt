package com.carlosnc.doctordroid.ui.components.battery

data class BatteryInfo(
    val level: Int,
    val health: String,
    val status: String,
    val plugged: String,
    val capacity: Long,
    val technology: String,
    val voltage: Int,
    val temperature: Int
)