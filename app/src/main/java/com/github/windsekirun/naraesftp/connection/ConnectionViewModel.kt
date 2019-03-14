package com.github.windsekirun.naraesftp.connection

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.composer.single.EnsureMainThreadSingleComposer
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.file.FileListActivity
import io.reactivex.rxkotlin.addTo
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

    fun tryConnection(item: ConnectionInfoItem, initial: Boolean = false) {
        val event = OpenProgressIndicatorDialog(getString(R.string.connection_connecting))
        postEvent(event)

        sessionController.connectionInfo = item
        sessionController.callback = F1 {
            postEvent(CloseProgressIndicatorDialog())
            if (it) {
                showSuccessConnection(item, initial)
                sessionController.callback = null
            } else {
                showFailConnection(item)
                sessionController.callback = null
            }
        }

        sessionController.connect()
    }

    fun clickConnectionLong(connectionInfoItem: ConnectionInfoItem) {
        val event = OpenConnectionEditDialog(connectionInfoItem) { state, _ ->
            if (state == -1) {
                removeConnection(connectionInfoItem)
            } else {
                editConnection(connectionInfoItem)
            }
        }

        postEvent(event)
    }

    fun clickAddConnection(view: View) {
        addNewConnection()
    }

    private fun loadData() {
        connectionInfoController.getListConnectionInfo()
            .compose(EnsureMainThreadSingleComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                if (data.isEmpty()) {
                    addNewConnection()
                } else {
                    connectionItems.clear()
                    connectionItems.addAll(data)
                }
            }.addTo(compositeDisposable)
    }

    private fun addNewConnection() {
        val event = OpenConnectionAddDialog {
            connectionInfoController.addConnectionInfo(it)
                .compose(EnsureMainThreadSingleComposer())
                .subscribe { data, error ->
                    if (error != null || data == null) {
                        return@subscribe
                    }

                    tryConnection(it, true)
                }.addTo(compositeDisposable)
        }

        postEvent(event)
    }

    private fun showSuccessConnection(item: ConnectionInfoItem, initial: Boolean) {
        val event = OpenConfirmDialog(getString(R.string.connection_success_autoconnect), {
            moveFileListActivity(false, item, initial)
        }) {
            moveFileListActivity(true, item, initial)
        }

        postEvent(event)
    }

    private fun showFailConnection(item: ConnectionInfoItem) {
        val event = OpenConfirmDialog(getString(R.string.connection_fail_retry)) {
            tryConnection(item)
        }

        postEvent(event)
    }

    private fun moveFileListActivity(autoConnect: Boolean, item: ConnectionInfoItem, initial: Boolean = false) {
        connectionInfoController.setLastConnectionInfo(item.id, autoConnect, initial)
            .compose(EnsureMainThreadSingleComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                sessionController.connectionInfo = data
                startActivity(FileListActivity::class.java)
                finishAllActivities()
            }.addTo(compositeDisposable)
    }

    private fun removeConnection(connectionInfoItem: ConnectionInfoItem) {
        connectionInfoController.removeConnectionInfo(connectionInfoItem.id)
            .compose(EnsureMainThreadSingleComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                showToast(getString(R.string.connection_removed))
                loadData()
            }.addTo(compositeDisposable)
    }

    private fun editConnection(connectionInfoItem: ConnectionInfoItem) {
        connectionInfoController.addConnectionInfo(connectionInfoItem)
            .compose(EnsureMainThreadSingleComposer())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                showToast(getString(R.string.connection_edited))
                loadData()
            }.addTo(compositeDisposable)
    }
}