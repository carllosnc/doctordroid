package com.carlosnc.doctordroid.modules.cpu

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.io.File

fun getCoreFrequency(coreIndex: Int): Long {
    return try {
        val path = "/sys/devices/system/cpu/cpu$coreIndex/cpufreq/scaling_cur_freq"
        val file = File(path)
        if (file.exists()) {
            file.readText().trim().toLong() / 1000 // Convert to MHz
        } else {
            0L
        }
    } catch (e: Exception) {
        0L
    }
}

fun getMaxCoreFrequency(coreIndex: Int): Long {
    return try {
        val path = "/sys/devices/system/cpu/cpu$coreIndex/cpufreq/scaling_max_freq"
        val file = File(path)
        if (file.exists()) {
            file.readText().trim().toLong() / 1000 // Convert to MHz
        } else {
            0L
        }
    } catch (e: Exception) {
        0L
    }
}
