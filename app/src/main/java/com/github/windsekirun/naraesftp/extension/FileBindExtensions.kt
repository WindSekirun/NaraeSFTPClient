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
    return (item.attrs.mTime.toLong() * 1000).asDateString("yyyy-MM-dd HH:mm:ss")
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

    private fun find(ext: String): Int {
        if (cacheMap.containsKey(ext)) return cacheMap[ext] ?: R.drawable.ic_document

        val resources = when {
            ext.contains(EXT_ANDROID) -> R.drawable.ic_android
            ext.contains(EXT_AUDIO) -> R.drawable.ic_audio
            ext.contains(EXT_C) -> R.drawable.ic_c
            ext.contains(EXT_CONSOLE) -> R.drawable.ic_console
            ext.contains(EXT_CPP) -> R.drawable.ic_cpp
            ext.contains(EXT_CSS) -> R.drawable.ic_css
            ext.contains(EXT_EMAIL) -> R.drawable.ic_email
            ext.contains(EXT_GRADLE) -> R.drawable.ic_gradle
            ext.contains(EXT_GROOVY) -> R.drawable.ic_groovy
            ext.contains(EXT_HTML) -> R.drawable.ic_html
            ext.contains(EXT_IMAGE) -> R.drawable.ic_image
            ext.contains(EXT_JAVA) -> R.drawable.ic_java
            ext.contains(EXT_JS) -> R.drawable.ic_javascript
            ext.contains(EXT_JSON) -> R.drawable.ic_json
            ext.contains(EXT_KOTLIN) -> R.drawable.ic_kotlin
            ext.contains(EXT_LESS) -> R.drawable.ic_less
            ext.contains(EXT_LOG) -> R.drawable.ic_log
            ext.contains(EXT_MARKDOWN) -> R.drawable.ic_markdown
            ext.contains(EXT_PDF) -> R.drawable.ic_pdf
            ext.contains(EXT_PHP) -> R.drawable.ic_php
            ext.contains(EXT_PPT) -> R.drawable.ic_powerpoint
            ext.contains(EXT_PYTHON) -> R.drawable.ic_python
            ext.contains(EXT_SWIFT) -> R.drawable.ic_swift
            ext.contains(EXT_TS) -> R.drawable.ic_typescript
            ext.contains(EXT_VIDEO) -> R.drawable.ic_video
            ext.contains(EXT_WORD) -> R.drawable.ic_word
            ext.contains(EXT_XML) -> R.drawable.ic_xml
            ext.contains(EXT_YAML) -> R.drawable.ic_yaml
            ext.contains(EXT_ZIP) -> R.drawable.ic_zip
            else -> R.drawable.ic_document
        }

        cacheMap[ext] = resources
        return resources
    }

    private val EXT_ANDROID = arrayOf("apk")
    private val EXT_AUDIO = arrayOf("mp3", "m4a", "flac", "wma", "aiff")
    private val EXT_C = arrayOf("c", "m")
    private val EXT_CONSOLE = arrayOf("sh", "zsh", "bash", "bat", "cmd", "awk", "fish")
    private val EXT_CPP = arrayOf("cc", "cpp", "mm", "cxx")
    private val EXT_CSS = arrayOf("css")
    private val EXT_EMAIL = arrayOf("ics", "eml", "mail")
    private val EXT_GRADLE = arrayOf("gradle")
    private val EXT_GROOVY = arrayOf("groovy")
    private val EXT_HTML = arrayOf("html", "htm")
    private val EXT_IMAGE =
        arrayOf("png", "jpg", "jpeg", "gif", "svg", "ico", "tif", "psd", "bmp", "webp")
    private val EXT_JAVA = arrayOf("java", "jar", "jsp")
    private val EXT_JS = arrayOf("js", "esc", "mjs")
    private val EXT_JSON = arrayOf("json")
    private val EXT_KOTLIN = arrayOf("kt", "kts")
    private val EXT_LESS = arrayOf("less")
    private val EXT_LOG = arrayOf("log")
    private val EXT_MARKDOWN = arrayOf("md", "markdown", "rst")
    private val EXT_PDF = arrayOf("pdf")
    private val EXT_PHP = arrayOf("php")
    private val EXT_PPT = arrayOf("pptx", "ppt")
    private val EXT_PYTHON = arrayOf("py")
    private val EXT_SWIFT = arrayOf("swift")
    private val EXT_TS = arrayOf("ts")
    private val EXT_VIDEO = arrayOf("webm", "mkv", "avi", "mov", "wmv", "mp4", "m4v", "mpeg")
    private val EXT_WORD = arrayOf("doc", "docx", "rtf")
    private val EXT_XML = arrayOf("xml", "plist", "xsl", "iml", "project")
    private val EXT_YAML = arrayOf("yaml", "YAML-tmLanguage", "yml")
    private val EXT_ZIP = arrayOf("zip", "tar", "gz", "xz", "bzip2", "gzip", "7z", "rar", "tgz")
}

private fun String.contains(target: Array<String>) = target.contains(this)