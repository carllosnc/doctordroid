package com.carlosnc.doctordroid.utils

import java.util.Locale

fun formatSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
        mb >= 1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
        else -> String.format(Locale.getDefault(), "%.2f KB", kb)
    }
}
