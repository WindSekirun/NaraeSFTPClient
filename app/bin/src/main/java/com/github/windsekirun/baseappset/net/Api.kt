@file:JvmName("Api")

package com.github.windsekirun.baseappset.net

import android.util.Base64
import com.google.gson.Gson

const val BASE_FILE = ""
const val FILE_ORIGINAL = "origin/"
const val FILE_100 = "100/"
const val FILE_240 = "240/"
const val FILE_480 = "480/"
const val FILE_720 = "720/"
const val FILE_1080 = "1080/"
const val PLAY_STORE = "market://details?id="

/**
 * getting server image path from relative path
 */
@JvmOverloads
fun getServerImagePath(path: String, resolution: String = FILE_480): String {
    if (path.contains(String.format("%s%s", BASE_FILE, resolution))) {
        return path
    }

    return String.format("%s%s%s", BASE_FILE, resolution, path)
}

fun <T> List<T>.toEncode() = Base64.encodeToString(Gson().toJson(this).toByteArray(charset("UTF-8")), Base64.NO_WRAP)

fun <T> toEncode(vararg items: T) =
    Base64.encodeToString(Gson().toJson(items.toList()).toByteArray(charset("UTF-8")), Base64.NO_WRAP)