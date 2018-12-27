package com.github.windsekirun.naraesftp.connection

import android.view.MenuItem
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.event.CloseProgressIndicatorDialog
import com.github.windsekirun.naraesftp.event.OpenConfirmDialog
import com.github.windsekirun.naraesftp.event.OpenConnectionAddDialog
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorDialog
import com.github.windsekirun.naraesftp.file.FileListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F1
import javax.inject.Inject

/**
 * NaraeSFTPClient
 * Class: ConnectionViewModel
 * Created by Pyxis on 2018-12-27.
 *
 *
 * Description:
 */

@InjectViewModel
class ConnectionViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val connectionItems = ObservableArrayList<ConnectionInfoItem>()

    @Inject
    lateinit var connectionInfoController: ConnectionInfoController
    @Inject
    lateinit var sessionController: SessionController

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadData()
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_connection_plus -> addNewConnection()
        }
        return true
    }

    fun tryConnection(item: ConnectionInfoItem) {
        val event = OpenProgressIndicatorDialog(getString(R.string.connection_connecting))
        postEvent(event)

        sessionController.connectionInfo = item
        sessionController.callback = F1 { it ->
            postEvent(CloseProgressIndicatorDialog())
            if (it) {
                showSuccessConnection(item)
                sessionController.callback = null
            } else {
                showFailConnection(item)
                sessionController.callback = null
            }
        }

        sessionController.connect()
    }

    private fun loadData() {
        connectionInfoController.getListConnectionInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                if (data.isEmpty()) {
                    addNewConnection()
                } else {
                    connectionItems.addAll(data)
                }
            }.addTo(compositeDisposable)
    }

    private fun addNewConnection() {
        val event = OpenConnectionAddDialog {
            connectionInfoController.addConnectionInfo(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    if (error != null || data == null) {
                        return@subscribe
                    }

                    tryConnection(it)
                }.addTo(compositeDisposable)
        }

        postEvent(event)
    }

    private fun showSuccessConnection(item: ConnectionInfoItem) {
        val event = OpenConfirmDialog(getString(R.string.connection_success_autoconnect), {
            moveFileListActivity(false, item)
        }) {
            moveFileListActivity(true, item)
        }

        postEvent(event)
    }

    private fun showFailConnection(item: ConnectionInfoItem) {
        val event = OpenConfirmDialog(getString(R.string.connection_fail_retry)) {
            tryConnection(item)
        }

        postEvent(event)
    }

    private fun moveFileListActivity(autoConnect: Boolean, item: ConnectionInfoItem) {
        connectionInfoController.setLastConnectionInfo(item.id, autoConnect)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                sessionController.connectionInfo = data
                startActivity(FileListActivity::class.java)
                finishAllActivities()
            }.addTo(compositeDisposable)
    }
}