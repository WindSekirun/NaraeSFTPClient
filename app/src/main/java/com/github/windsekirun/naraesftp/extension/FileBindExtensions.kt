@file:JvmName("FileBindExtensions")

package com.github.windsekirun.naraesftp.extension

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.naraesftp.R
import com.jcraft.jsch.ChannelSftp
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.getFileExtension
import kotlin.math.roundToInt


fun getReadableSize(item: ChannelSftp.LsEntry): String {
    val size = item.attrs.size
    if (size <= 0L) return "0B"

    val units = listOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val digitGroup: Int = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
    val result = size / Math.pow(1024.toDouble(), digitGroup.toDouble()).roundToInt()
    return "%d%s".format(result, units[digitGroup])
}

fun getReadablePermision(item: ChannelSftp.LsEntry): String {
    return item.attrs.permissionsString
}

fun getReadableDate(item: ChannelSftp.LsEntry): String {
    return (item.attrs.mTime.toLong() * 1000).asDateString("yyyy-MM-dd")
}

fun getIcon(item: ChannelSftp.LsEntry): Drawable? {
    val context = ActivityReference.getContext()
    return if (isDirectory(item)) {
        ContextCompat.getDrawable(context, R.drawable.ic_folder_other)
    } else {
        ContextCompat.getDrawable(context, FileIconMatcher.find(item))
    }
}

/**
 * Check target [ChannelSftp.LsEntry] is folder
 */
fun isDirectory(item: ChannelSftp.LsEntry): Boolean {
    return item.attrs.isDir || item.filename.trim() == ".."
}

object FileIconMatcher {
    private val cacheMap = HashMap<String, Int>()

    fun find(item: ChannelSftp.LsEntry): Int = find(item.filename.getFileExtension())

    private fun find(extension: String): Int {
        if (cacheMap.containsKey(extension)) return cacheMap[extension] ?: R.drawable.ic_document

        val resources = when {
            extension.containsList("apk")
            -> R.drawable.ic_android
            extension.containsList("mp3", "m4a", "flac", "wma", "aiff")
            -> R.drawable.ic_audio
            extension.containsList("c", "m")
            -> R.drawable.ic_c
            extension.containsList("sh", "ksh", "csh", "tcsh", "zsh", "bash", "bat", "cmd", "awk", "fish")
            -> R.drawable.ic_console
            extension.containsList("cc", "cpp", "mm", "cxx")
            -> R.drawable.ic_cpp
            extension.containsList("css")
            -> R.drawable.ic_css
            extension.containsList("ics", "eml", "mail")
            -> R.drawable.ic_email
            extension.containsList("gradle")
            -> R.drawable.ic_gradle
            extension.containsList("groovy")
            -> R.drawable.ic_groovy
            extension.containsList("html", "htm")
            -> R.drawable.ic_html
            extension.containsList("png", "jpg", "jpeg", "gif", "svg", "ico", "tif", "tiff", "psd", "bmp", "webp")
            -> R.drawable.ic_image
            extension.containsList("java", "jar", "jsp")
            -> R.drawable.ic_java
            extension.containsList("js", "esx", "mjs")
            -> R.drawable.ic_javascript
            extension.containsList("json")
            -> R.drawable.ic_json
            extension.containsList("kt", "kts")
            -> R.drawable.ic_kotlin
            extension.containsList("less")
            -> R.drawable.ic_less
            extension.containsList("log")
            -> R.drawable.ic_log
            extension.containsList("md", "markdown", "rst")
            -> R.drawable.ic_markdown
            extension.containsList("pdf")
            -> R.drawable.ic_pdf
            extension.containsList("php")
            -> R.drawable.ic_php
            extension.containsList("pptx", "ppt")
            -> R.drawable.ic_powerpoint
            extension.containsList("py")
            -> R.drawable.ic_python
            extension.containsList("swit")
            -> R.drawable.ic_swift
            extension.containsList("ts")
            -> R.drawable.ic_typescript
            extension.containsList("webm", "mkv", "avi", "mov", "wmv", "mp4", "m4v", "mpeg")
            -> R.drawable.ic_video
            extension.containsList("doc", "docx", "rtf")
            -> R.drawable.ic_word
            extension.containsList("xml", "plist", "xsl", "iml", "project")
            -> R.drawable.ic_xml
            extension.containsList("yaml", "YAML-tmLanguage", "yml")
            -> R.drawable.ic_yaml
            extension.containsList("zip", "tar", "gz", "xz", "bzip2", "gzip", "7z", "rar", "tgz")
            -> R.drawable.ic_zip
            else -> R.drawable.ic_document
        }

        cacheMap[extension] = resources
        return resources
    }
}

private fun String.containsList(vararg target: String) = target.contains(this)