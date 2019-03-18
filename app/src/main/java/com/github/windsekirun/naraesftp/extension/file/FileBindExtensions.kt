@file:JvmName("FileBindExtensions")

package com.github.windsekirun.naraesftp.extension.file

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.naraesftp.R
import com.jcraft.jsch.ChannelSftp
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.getFileExtension
import java.io.File
import kotlin.math.roundToInt

private val folderIcon: Drawable? by lazy {
    ContextCompat.getDrawable(ActivityReference.getContext(), R.drawable.ic_folder_other)
}

fun getReadableSize(item: ChannelSftp.LsEntry) = getReadableSize(item.attrs.size)
fun getReadableSize(item: File) = getReadableSize(item.length())

fun getReadableDate(item: ChannelSftp.LsEntry): String = getReadableDate(item.attrs.mTime.toLong())
fun getReadableDate(item: File) = getReadableDate(item.lastModified())

fun getReadablePermission(item: ChannelSftp.LsEntry): String {
    return item.attrs.permissionsString
}

fun getIcon(item: ChannelSftp.LsEntry): Drawable? {
    val context = ActivityReference.getContext()
    return if (isDirectory(item)) {
        folderIcon
    } else {
        ContextCompat.getDrawable(
            context,
            FileIconMatcher.find(item.filename.getFileExtension())
        )
    }
}

fun getIcon(item: File): Drawable? {
    val context = ActivityReference.getContext()
    return if (isDirectory(item)) {
        folderIcon
    } else {
        ContextCompat.getDrawable(
            context,
            FileIconMatcher.find(item.extension)
        )
    }
}

fun isDirectory(item: ChannelSftp.LsEntry): Boolean {
    return item.attrs.isDir || item.filename.trim() == ".."
}

fun isDirectory(item: File): Boolean {
    return item.isDirectory
}

private fun getReadableSize(size: Long): String {
    if (size <= 0L) return "0B"

    val units = listOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val digitGroup: Int = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
    val result = size / Math.pow(1024.toDouble(), digitGroup.toDouble()).roundToInt()
    return "%d%s".format(result, units[digitGroup])
}

private fun getReadableDate(modifiedTime: Long): String {
    return (modifiedTime * 1000).asDateString("yyyy.MM.dd HH:mm:ss")
}