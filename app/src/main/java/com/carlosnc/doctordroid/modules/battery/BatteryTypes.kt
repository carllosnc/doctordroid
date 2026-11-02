package com.carlosnc.doctordroid.modules.battery

data class BatteryInfo(
    val level: Int,
    val health: String,
    val status: String,
    val plugged: String,
    val capacity: Long,
    val technology: String,
    val voltage: Int, // in mV
    val temperature: Int, // in tenths of °C
    val currentNowMa: Int, // in mA (positive = charging, negative = discharging)
    val powerWatts: Float, // in Watts
    val cycleCount: Int // battery charge cycles (-1 if unsupported by OEM)
)
