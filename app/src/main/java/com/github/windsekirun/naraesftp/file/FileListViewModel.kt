package com.github.windsekirun.naraesftp.file

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.argsinjector.Extra
import com.github.windsekirun.baseapp.module.back.DoubleBackInvoker
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.connection.ConnectionActivity
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.extension.isDirectory
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
import com.jcraft.jsch.ChannelSftp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F0
import pyxis.uzuki.live.richutilskt.impl.F1
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
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
    val path = ObservableString()

    @Inject lateinit var sessionController: SessionController
    @Inject lateinit var connectionInfoController: ConnectionInfoController

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadData(sessionController.connectionInfo.initialDirectory)

        path.propertyChanges()
            .filter { path.get() == "//"}
            .subscribe { _, _ -> path.set("/") }
            .addTo(compositeDisposable)
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_file_logout -> confirmDisconnect()
        }
        return true
    }

    fun onBackPressed() {
        val currentPath = sessionController.sFtpController.currentPath
        if (currentPath != "/") {
            val list = currentPath.split("/").toMutableList()
            if (list.last() == "") list.removeAt(list.lastIndex)
            list.removeAt(list.lastIndex)

            var newPath = TextUtils.join("/", list)
            if (newPath == "") newPath += "/"
            if (newPath.last() != '/') newPath += "/"
            loadData(newPath, true)
        } else {
            DoubleBackInvoker.execute(getString(R.string.file_list_double_back))
        }
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

    private fun loadData(path: String = "", backward: Boolean = false) {
        if (!sessionController.isConnected()) {
            showToast(getString(R.string.file_list_disconnected))
            tryDisconnect()
            return
        }

        val event = OpenProgressIndicatorDialog(getString(R.string.file_list_loading))
        postEvent(event)

        sessionController.getListRemoteFiles(path, backward)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    postEvent(CloseProgressIndicatorDialog())
                    postEvent(ScrollUpEvent())
                    return@subscribe
                }

                entries.clear()
                entries.addAll(data)
                this.path.set(sessionController.sFtpController.currentPath)
                postEvent(CloseProgressIndicatorDialog())
                postEvent(ScrollUpEvent())
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
                val authority = requireActivity().packageName + ".fileprovider"
                FileProvider.getUriForFile(requireActivity(), authority, file)
            } else {
                Uri.fromFile(file)
            }

            val myMime = MimeTypeMap.getSingleton()
            val mimeType = myMime.getMimeTypeFromExtension(file.extension)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val apkFlag = file.extension.contains("apk")

            if (apkFlag) {
                val permissions = if (Build.VERSION.SDK_INT >= 23) Manifest.permission.REQUEST_INSTALL_PACKAGES else
                    Manifest.permission.READ_EXTERNAL_STORAGE

                requestPermission(F1<Int> {
                    if (Build.VERSION.SDK_INT >= 26 && !requireActivity().packageManager.canRequestPackageInstalls()) {
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                Uri.parse("package:${requireActivity().packageName}")
                            )
                        )
                    } else {
                        intent.action = Intent.ACTION_VIEW
                        startActivity(intent)
                    }
                }, permissions)
            } else {
                val chooser = Intent.createChooser(intent, "Open with...")
                startActivity(chooser)
            }
        }

        postEvent(event)
    }
}