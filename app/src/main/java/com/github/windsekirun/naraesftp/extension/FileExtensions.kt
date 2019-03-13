@file:JvmName("FileExtensions")

package com.github.windsekirun.naraesftp.extension

import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

/**
 * NaraeSFTPClient
 * Class: FileExtensions
 * Created by Pyxis on 2019-03-13.
 *
 * Description:
 */

fun File.toUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= 24) {
        val authority = context.packageName + ".fileprovider"
        FileProvider.getUriForFile(context, authority, this)
    } else {
        Uri.fromFile(this)
    }
}

fun File.getMimeType(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension) ?: "application/octet-stream"
}