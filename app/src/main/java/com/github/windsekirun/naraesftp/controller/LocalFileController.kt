package com.github.windsekirun.naraesftp.controller

import android.os.Environment
import android.text.TextUtils
import io.reactivex.Observable
import java.io.File

/**
 * NaraeSFTPClient
 * Class: LocalFileController
 * Created by Pyxis on 2019-03-18.
 *
 * Description:
 */
class LocalFileController {

    var currentPath = Environment.getExternalStorageDirectory().absolutePath

    fun resetPathToRoot() {
        currentPath = Environment.getExternalStorageDirectory().absolutePath
    }

    fun appendToPath(path: String) {
        currentPath += path
    }

    fun getListLocalFiles(path: String, backward: Boolean = false): Observable<List<File>> {
        currentPath = if (TextUtils.isEmpty(path)) currentPath else if (backward) path else "$currentPath$path/"
        currentPath = currentPath.replace("//", "/")
        return Observable.create<List<File>> { emitter ->
            val file = File(currentPath)
            val arrays = file.listFiles()
            emitter.onNext(arrays.toList())
        }
    }
}