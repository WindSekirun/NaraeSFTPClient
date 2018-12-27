package com.github.windsekirun.naraesftp.file

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.connection.ConnectionActivity
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorDialog
import com.jcraft.jsch.ChannelSftp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

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

    @Inject
    lateinit var sessionController: SessionController

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadData()
    }

    private fun loadData(path: String = "") {
        if (!sessionController.isConnected()) {
            showToast(getString(R.string.file_list_disconnected))
            startActivity(ConnectionActivity::class.java)
            finishAllActivities()
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
                this.path.set(sessionController.sFtpController.currentPath)
            }.addTo(compositeDisposable)
    }
}