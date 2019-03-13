package com.github.windsekirun.naraesftp.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.windsekirun.naraesftp.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.io.File

/**
 * NaraeSFTPClient
 * Class: FileOpener
 * Created by Pyxis on 2019-03-13.
 *
 * Description:
 */
object FileOpener {
    private val compositeDisposable = CompositeDisposable()

    fun dispose() {
        compositeDisposable.clear()
    }

    fun openFile(file: File, activity: AppCompatActivity) {
        val uri = file.toUri(activity)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, file.getMimeType())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (file.extension.contains("apk")) {
            if (Build.VERSION.SDK_INT >= 26 && !activity.packageManager.canRequestPackageInstalls()) {
                showRequestUnknownAppRequest(intent, activity)
            } else {
                openApk(intent, activity)
            }
            return
        }

        activity.startActivity(Intent.createChooser(intent, "Open with..."))
    }

    private fun showRequestUnknownAppRequest(intent: Intent, activity: AppCompatActivity) {
        val builder = AlertDialog.Builder(activity).apply {
            setMessage(activity.getString(R.string.request_package_message))
            setCancelable(false)
            setPositiveButton(activity.getString(R.string.request_package_positivie)) { _, _ ->
                requestUnknownAppSources(intent, activity)
            }
            setNegativeButton(android.R.string.cancel) { _, _ -> }
        }

        builder.show()
    }

    private fun requestUnknownAppSources(intent: Intent, activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT < 26) return

        RxActivityResult.result()
            .subscribe { data, _ ->
                if (data.resultCode != Activity.RESULT_OK) return@subscribe
                openApk(intent, activity)
            }.addTo(compositeDisposable)

        RxActivityResult.startActivityForResult(
            Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${activity.packageName}")
            )
        )
    }

    private fun openApk(intent: Intent, activity: AppCompatActivity) {
        intent.action = Intent.ACTION_VIEW
        activity.startActivity(intent)
    }
}