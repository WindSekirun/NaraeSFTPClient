package com.github.windsekirun.naraesftp.file

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.back.DoubleBackInvoker
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.connection.ConnectionActivity
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.event.CloseProgressIndicatorDialog
import com.github.windsekirun.naraesftp.event.OpenConfirmDialog
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorDialog
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorPercentDialog
import com.github.windsekirun.naraesftp.extension.isDirectory
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
import com.jcraft.jsch.ChannelSftp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F0
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
import com.github.windsekirun.naraesftp.R
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * NaraeSFTPClient
 * Class: FileListViewModel
 * Created by Pyxis on 2018-12-27.
 *
 *
 * Description:
 */

@InjectViewModel
class FileListViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val entries = ObservableArrayList<ChannelSftp.LsEntry>()

    @Inject
    lateinit var sessionController: SessionController
    @Inject
    lateinit var connectionInfoController: ConnectionInfoController

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadData()
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_file_logout -> confirmDisconnect()
        }
        return true
    }

    fun onBackPressed() {
        DoubleBackInvoker.execute(getString(R.string.file_list_double_back))
    }

    fun clickEntry(item: ChannelSftp.LsEntry) {
        if (isDirectory(item)) {
            loadData(item.filename)
        } else {
            requestPermission(
                F0 { confirmDownloadDialog(item) },
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    fun startDownload(dialog: ProgressIndicatorPercentDialog, item: ChannelSftp.LsEntry) {
        val file = "${Environment.getExternalStorageDirectory()}/NaraeSFTP/%s".format(item.filename).toFile()
        file.parentFile.mkdirs()

        sessionController.downloadFile(item.filename, file.absolutePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                dialog.setPercent(data.progress.roundToInt())
            }, { error ->
                Log.e(FileListViewModel::class.java.simpleName, "error: ${error.message}", error)
                showToast(getString(R.string.file_list_failed))
                dialog.dismiss()
            }, {
                dialog.dismiss()
                openFile(file)
            })
            .addTo(compositeDisposable)
    }

    private fun loadData(path: String = "") {
        if (!sessionController.isConnected()) {
            showToast(getString(R.string.file_list_disconnected))
            tryDisconnect()
            return
        }

        val event = OpenProgressIndicatorDialog(getString(R.string.file_list_loading))
        postEvent(event)

        sessionController.getListRemoteFiles(path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe

                entries.clear()
                entries.addAll(data)
                postEvent(CloseProgressIndicatorDialog())
            }.addTo(compositeDisposable)
    }

    private fun confirmDisconnect() {
        val event = OpenConfirmDialog(getString(R.string.file_list_logout)) {
            tryDisconnect()
        }

        postEvent(event)
    }

    private fun tryDisconnect() {
        val connectionInfo = sessionController.connectionInfo
        connectionInfoController.setAutoConnectionFlag(connectionInfo.id, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe

                startActivity(ConnectionActivity::class.java)
                finishAllActivities()
            }.addTo(compositeDisposable)
    }

    private fun confirmDownloadDialog(item: ChannelSftp.LsEntry) {
        val basePath = "${Environment.getExternalStorageDirectory()}/NaraeSFTP"
        val event = OpenConfirmDialog(getString(R.string.file_list_confirm_dialog).format(item.filename, basePath)) {
            showDownloadDialog(item)
        }

        postEvent(event)
    }

    private fun showDownloadDialog(item: ChannelSftp.LsEntry) {
        val event = OpenProgressIndicatorPercentDialog(getString(R.string.file_list_downloading), item)
        postEvent(event)
    }

    private fun openFile(file: File) {
        val event = OpenConfirmDialog(getString(R.string.file_list_open)) {
            val uri = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    getApplication(),
                    getApplication<MainApplication>().packageName + ".fileprovider", file
                )
            } else {
                Uri.fromFile(file)
            }

            val myMime = MimeTypeMap.getSingleton()
            val newIntent = Intent(Intent.ACTION_VIEW)
            val mimeType = myMime.getMimeTypeFromExtension(file.extension)?.substring(1)

            newIntent.setDataAndType(uri, mimeType)
            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            if (newIntent.resolveActivity(getApplication<MainApplication>().packageManager) != null) {
                startActivity(newIntent)
            } else {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://play.google.com/store/search?q=$mimeType")
                    setPackage("com.android.vending")
                }
                startActivity(intent)
            }
        }

        postEvent(event)
    }
}