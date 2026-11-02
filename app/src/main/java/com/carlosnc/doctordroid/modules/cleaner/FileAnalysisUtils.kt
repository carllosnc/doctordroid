package com.carlosnc.doctordroid.modules.cleaner

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector
import java.io.File

data class FileCategory(
    val name: String,
    val sizeBytes: Long,
    val fileCount: Int,
    val icon: ImageVector
)

fun scanFileCategories(context: Context): List<FileCategory> {
    val resolver = context.contentResolver

    val images = scanMediaSize(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    val videos = scanMediaSize(resolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
    val audio = scanMediaSize(resolver, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
    val documents = scanDocumentsSize(resolver)
    val archives = scanArchivesSize(resolver)
    val apks = scanApksSize(context)

    val totalOther = mutableListOf(images, videos, audio, documents, archives, apks)
    val totalKnown = totalOther.sumOf { it.sizeBytes }
    val totalStorage = getTotalStorage()

    val other = (totalStorage - totalKnown).coerceAtLeast(0L)

    return listOf(
        FileCategory("Images", images.sizeBytes, images.fileCount, Icons.Outlined.Image),
        FileCategory("Videos", videos.sizeBytes, videos.fileCount, Icons.Outlined.Movie),
        FileCategory("Audio", audio.sizeBytes, audio.fileCount, Icons.Outlined.MusicNote),
        FileCategory("Documents", documents.sizeBytes, documents.fileCount, Icons.Outlined.Description),
        FileCategory("Archives", archives.sizeBytes, archives.fileCount, Icons.Outlined.Archive),
        FileCategory("APK Files", apks.sizeBytes, apks.fileCount, Icons.Outlined.Android),
        FileCategory("Other", other, 0, Icons.Outlined.AudioFile)
    )
}

private data class ScanResult(val sizeBytes: Long, val fileCount: Int)

private fun scanMediaSize(resolver: ContentResolver, uri: Uri): ScanResult {
    var totalSize = 0L
    var count = 0
    try {
        val projection = arrayOf(MediaStore.MediaColumns.SIZE)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            while (cursor.moveToNext()) {
                totalSize += cursor.getLong(sizeCol)
                count++
            }
        }
    } catch (_: Exception) {}
    return ScanResult(totalSize, count)
}

private fun scanDocumentsSize(resolver: ContentResolver): ScanResult {
    var totalSize = 0L
    var count = 0
    try {
        val projection = arrayOf(MediaStore.Files.FileColumns.SIZE)
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ? OR " +
                "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ? OR " +
                "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ? OR " +
                "${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR " +
                "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selArgs = arrayOf(
            "application/pdf%",
            "application/msword%",
            "application/vnd.openxmlformats%",
            "text/plain",
            "application/vnd.ms-excel%"
        )

        resolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, selection, selArgs, null
        )?.use { cursor ->
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            while (cursor.moveToNext()) {
                totalSize += cursor.getLong(sizeCol)
                count++
            }
        }
    } catch (_: Exception) {}
    return ScanResult(totalSize, count)
}

private fun scanArchivesSize(resolver: ContentResolver): ScanResult {
    var totalSize = 0L
    var count = 0
    try {
        val projection = arrayOf(MediaStore.Files.FileColumns.SIZE)
        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} IN (?,?,?,?,?)"
        val selArgs = arrayOf(
            "application/zip",
            "application/x-rar-compressed",
            "application/x-7z-compressed",
            "application/gzip",
            "application/x-tar"
        )
        resolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, selection, selArgs, null
        )?.use { cursor ->
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            while (cursor.moveToNext()) {
                totalSize += cursor.getLong(sizeCol)
                count++
            }
        }
    } catch (_: Exception) {}
    return ScanResult(totalSize, count)
}

private fun scanApksSize(context: Context): ScanResult {
    var totalSize = 0L
    var count = 0
    try {
        val dirs = listOf(
            File(Environment.getExternalStorageDirectory(), "Download"),
            File("/storage/emulated/0/Download")
        )
        for (dir in dirs) {
            if (dir.exists()) {
                dir.listFiles()?.filter { it.extension.equals("apk", ignoreCase = true) }?.forEach {
                    totalSize += it.length()
                    count++
                }
            }
        }
    } catch (_: Exception) {}
    return ScanResult(totalSize, count)
}

private fun getTotalStorage(): Long {
    try {
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.totalBytes - stat.availableBytes
    } catch (_: Exception) {}
    return 0L
}
