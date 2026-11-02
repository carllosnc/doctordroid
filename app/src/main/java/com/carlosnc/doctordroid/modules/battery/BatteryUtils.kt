package com.carlosnc.doctordroid.modules.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import kotlin.math.abs

fun getBatteryTemperature(context: Context): Float {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    return (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
}

fun getBatteryInfo(context: Context): BatteryInfo {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, intentFilter)

    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val healthInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
    val statusInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val pluggedInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
    val technology = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
    val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: 3800
    val temperature = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: 250

    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    val chargeCounter = try {
        batteryManager?.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) ?: -1L
    } catch (_: Exception) {
        -1L
    }

    val capacity = if (level > 0 && scale > 0 && chargeCounter > 0) {
        ((chargeCounter.toDouble() / (level.toDouble() / scale.toDouble())) / 1000.0).toLong()
    } else {
        5000L // Typical modern capacity fallback
    }

    // Current in microamperes or milliamperes depending on OEM
    val rawCurrent = try {
        batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0
    } catch (_: Exception) {
        0
    }
    var currentMa = if (abs(rawCurrent) > 10000) {
        rawCurrent / 1000 // Convert uA to mA
    } else {
        rawCurrent
    }

    val isCharging = statusInt == BatteryManager.BATTERY_STATUS_CHARGING
    // Normalize current sign: positive when charging, negative when discharging
    if (isCharging && currentMa < 0) {
        currentMa = -currentMa
    } else if (!isCharging && currentMa > 0) {
        currentMa = -currentMa
    }

    // Power in Watts: (mV * mA) / 1,000,000
    val powerWatts = if (voltage > 0 && currentMa != 0) {
        (voltage.toFloat() * abs(currentMa).toFloat()) / 1_000_000f
    } else {
        0f
    }

    // Battery Cycle Count (Android 14+ / API 34+, property ID 7)
    // Protected by BATTERY_STATS on some OEM builds; safely catch SecurityException
    val cycleCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        try {
            val cycles = batteryManager?.getIntProperty(7) ?: -1
            if (cycles > 0) cycles else -1
        } catch (_: Exception) {
            -1
        }
    } else {
        -1
    }

    val health = when (healthInt) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Good"
    }

    val status = when (statusInt) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        else -> "Unplugged"
    }

    val plugged = when (pluggedInt) {
        BatteryManager.BATTERY_PLUGGED_AC -> "Fast Charger (AC)"
        BatteryManager.BATTERY_PLUGGED_USB -> "USB Cable"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
        else -> "Battery Power"
    }

    val batteryLevel = if (level != -1 && scale != -1) {
        (level * 100 / scale.toFloat()).toInt()
    } else {
        50
    }

    return BatteryInfo(
        level = batteryLevel,
        health = health,
        status = status,
        plugged = plugged,
        capacity = capacity,
        technology = technology,
        voltage = voltage,
        temperature = temperature,
        currentNowMa = currentMa,
        powerWatts = powerWatts,
        cycleCount = cycleCount
    )
}
