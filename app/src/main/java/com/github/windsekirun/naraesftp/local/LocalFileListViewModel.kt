package com.github.windsekirun.naraesftp.local

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.composer.EnsureMainThreadComposer
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.controller.LocalFileController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.extension.file.isDirectory
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
import io.reactivex.rxkotlin.addTo
import pyxis.uzuki.live.richutilskt.impl.F0
import pyxis.uzuki.live.richutilskt.utils.put
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

@InjectViewModel
class LocalFileListViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val entries = ObservableArrayList<File>()
    val path = ObservableString()
    val hasData = ObservableBoolean()

    @Inject lateinit var sessionController: SessionController
    @Inject lateinit var localFileController: LocalFileController

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadData(localFileController.currentPath)
    }

    fun onBackPressed() {

    }

    fun clickEntry(item: File) {
        if (isDirectory(item)) {
            loadData(item.name)
        } else {
            requestPermission(
                F0 { confirmUploadDialog(item) },
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun startUpload(dialog: ProgressIndicatorPercentDialog, item: File) {
        sessionController.uploadFile(item)
            .subscribe({ data ->
                dialog.setPercent(data.progress.roundToInt())
            }, { error ->
                Log.e(LocalFileListViewModel::class.java.simpleName, "error: ${error.message}", error)
                showToast(getString(R.string.file_list_failed))
                dialog.dismiss()
            }, {
                dialog.dismiss()
                passPreviousData(item)
            }).addTo(compositeDisposable)
    }

    private fun loadData(path: String = "", backward: Boolean = false) {
        postEvent(OpenProgressIndicatorDialog(getString(R.string.file_list_loading)))

        localFileController.getListLocalFiles(path, backward)
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
                this.path.set(localFileController.currentPath)
                postEvent(CloseProgressIndicatorDialog())
                postEvent(ScrollUpEvent())
            }.addTo(compositeDisposable)
    }

    private fun confirmUploadDialog(item: File) {
        val currentPath = sessionController.sFtpController.currentPath
        val event = OpenConfirmDialog(getString(R.string.local_file_confirm_upload).format(item.name, currentPath)) {
            val event = OpenProgressIndicatorPercentFileDialog(getString(R.string.file_uploading), item)
            postEvent(event)
        }

        postEvent(event)
    }

    private fun passPreviousData(item: File) {
        val bundle = Bundle()
        bundle.put(UPLOAD_PATH, item.name)
        setResult(Activity.RESULT_OK, bundle)
        finishActivity(LocalFileListActivity::class.java)
    }

    companion object {
        const val UPLOAD_PATH = "f5320bbf-2ff5-4c4f-b918-83ed197ea8a3"
    }
}