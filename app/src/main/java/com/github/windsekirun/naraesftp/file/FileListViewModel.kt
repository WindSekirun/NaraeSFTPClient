package com.github.windsekirun.naraesftp.file

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.back.DoubleBackInvoker
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.module.composer.single.EnsureMainThreadSingleComposer
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.connection.ConnectionActivity
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.extension.FileOpener
import com.github.windsekirun.naraesftp.extension.RxActivityResult
import com.github.windsekirun.naraesftp.extension.file.isDirectory
import com.github.windsekirun.naraesftp.local.LocalFileListActivity
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
import com.jcraft.jsch.ChannelSftp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F0
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * NaraeSFTPClient
 * Class: FileListViewModel
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */

@InjectViewModel
class FileListViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val entries = ObservableArrayList<ChannelSftp.LsEntry>()
    val path = ObservableString()
    val filterEnable = ObservableBoolean()
    val hasData = ObservableBoolean()

    @Inject lateinit var sessionController: SessionController
    @Inject lateinit var connectionInfoController: ConnectionInfoController

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadData(sessionController.connectionInfo.initialDirectory)

        path.propertyChanges()
            .filter { path.get() == "//" }
            .subscribe { _, _ -> path.set("/") }
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        FileOpener.dispose()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        FileOpener.dispose()
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_file_logout -> confirmDisconnect()
        }
        return true
    }

    fun onBackPressed() {
        val currentPath = sessionController.sFtpController.currentPath
        val initialDirectory = sessionController.connectionInfo.initialDirectory.let {
            if (it.last() != '/') "$it/" else it
        }

        if (currentPath != initialDirectory) {
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

    fun clickFilter(view: View) {

    }

    fun clickFilterDisable(view: View) {
        filterEnable.set(false)
        loadData(path.get(), false)
    }

    fun clickUpload(view: View) {
        RxActivityResult.result()
            .subscribe { data, error ->
                if (data == null || data.resultCode != Activity.RESULT_OK) return@subscribe

            }.addTo(compositeDisposable)

        RxActivityResult.startActivityForResult(
            Intent(requireActivity(), LocalFileListActivity::class.java),
            requestCode = 27
        )
    }

    fun clickCreateDirectory(view: View) {
        MaterialDialog(requireActivity()).show {
            title(R.string.file_create_directory)
            input { _, text ->
                tryCreateDirectory(text)
            }
            positiveButton(R.string.submit)
            onDismiss { postEvent(HideSheetEvent()) }
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

        postEvent(OpenProgressIndicatorDialog(getString(R.string.file_list_loading)))

        sessionController.getListRemoteFiles(path, backward)
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    hasData.set(false)
                    postEvent(CloseProgressIndicatorDialog())
                    postEvent(ScrollUpEvent())
                    return@subscribe
                }

                entries.clear()
                entries.addAll(data)
                hasData.set(entries.isNotEmpty())
                this.path.set(sessionController.sFtpController.currentPath)
                postEvent(CloseProgressIndicatorDialog())
                postEvent(ScrollUpEvent())
            }.addTo(compositeDisposable)
    }

    private fun confirmDisconnect() {
        postEvent(OpenConfirmDialog(getString(R.string.file_list_logout)) { tryDisconnect() })
    }

    private fun tryDisconnect() {
        val connectionInfo = sessionController.connectionInfo
        connectionInfoController.setAutoConnectionFlag(connectionInfo.id, false)
            .compose(EnsureMainThreadSingleComposer())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe
                sessionController.sFtpController.resetPathToRoot()

                startActivity(ConnectionActivity::class.java)
                finishAllActivities()
            }.addTo(compositeDisposable)
    }

    private fun confirmDownloadDialog(item: ChannelSftp.LsEntry) {
        val basePath = "${Environment.getExternalStorageDirectory()}/NaraeSFTP"
        val event = OpenConfirmDialog(getString(R.string.file_list_confirm_dialog).format(item.filename, basePath)) {
            val event = OpenProgressIndicatorPercentDialog(getString(R.string.file_list_downloading), item)
            postEvent(event)
        }

        postEvent(event)
    }

    private fun openFile(file: File) {
        val event = OpenConfirmDialog(getString(R.string.file_list_open)) {
            FileOpener.openFile(file, requireAppCompatActivity())
        }

        postEvent(event)
    }

    private fun tryCreateDirectory(text: CharSequence) {
        sessionController.createDirectory(text.toString())
            .compose(EnsureMainThreadComposer())
            .subscribe { data, error ->
                if (error != null || data == null) return@subscribe

                showToast(getString(R.string.file_created_directory))
                loadData(sessionController.sFtpController.currentPath, true)
            }.addTo(compositeDisposable)
    }
}