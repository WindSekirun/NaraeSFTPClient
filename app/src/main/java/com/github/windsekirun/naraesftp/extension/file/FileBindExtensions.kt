@file:JvmName("FileBindExtensions")

package com.github.windsekirun.naraesftp.extension.file

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.naraesftp.R
import com.jcraft.jsch.ChannelSftp
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.getFileExtension
import kotlin.math.roundToInt

private val folderIcon: Drawable? by lazy {
    ContextCompat.getDrawable(ActivityReference.getContext(), R.drawable.ic_folder_other)
}

fun getReadableSize(item: ChannelSftp.LsEntry): String {
    val size = item.attrs.size
    if (size <= 0L) return "0B"

    val units = listOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val digitGroup: Int = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
    val result = size / Math.pow(1024.toDouble(), digitGroup.toDouble()).roundToInt()
    return "%d%s".format(result, units[digitGroup])
}

fun getReadablePermission(item: ChannelSftp.LsEntry): String {
    return item.attrs.permissionsString
}

fun getReadableDate(item: ChannelSftp.LsEntry): String {
    return (item.attrs.mTime.toLong() * 1000).asDateString("yyyy.MM.dd HH:mm:ss")
}

fun getIcon(item: ChannelSftp.LsEntry): Drawable? {
    val context = ActivityReference.getContext()
    return if (isDirectory(item)) {
        folderIcon
    } else {
        ContextCompat.getDrawable(context,
            FileIconMatcher.find(item.filename.getFileExtension())
        )
    }
}

fun isDirectory(item: ChannelSftp.LsEntry): Boolean {
    return item.attrs.isDir || item.filename.trim() == ".."
}