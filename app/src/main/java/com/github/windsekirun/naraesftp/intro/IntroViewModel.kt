package com.github.windsekirun.naraesftp.intro

import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.connection.ConnectionActivity
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SessionController
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorDialog
import com.github.windsekirun.naraesftp.file.FileListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.impl.F1
import pyxis.uzuki.live.richutilskt.utils.runDelayedOnUiThread
import javax.inject.Inject

/**
 * NaraeSFTPClient
 * Class: IntroViewModel
 * Created by Pyxis on 2018-12-27.
 *
 *
 * Description:
 */

@InjectViewModel
class IntroViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {

    @Inject
    lateinit var connectionInfoController: ConnectionInfoController
    @Inject
    lateinit var sessionController: SessionController

    override fun onCreate(owner: LifecycleOwner) {
        runDelayedOnUiThread({
            checkConnectionAvailable()
        }, 1000)
    }

    private fun checkConnectionAvailable() {
        connectionInfoController.findLastConnectionItem()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, _ ->
                if (data != null) {
                    tryConnection(data)
                    return@subscribe
                }

                moveConnectionActivity(null)
            }.addTo(compositeDisposable)
    }

    private fun tryConnection(item: ConnectionInfoItem) {
        val event = OpenProgressIndicatorDialog(getString(R.string.connection_connecting))
        postEvent(event)

        sessionController.connectionInfo = item
        sessionController.callback = F1 { it ->
            if (it) {
                moveFileListActivity(true, item)
                sessionController.callback = null
            } else {
                moveConnectionActivity(item)
                sessionController.callback = null
            }
        }

        sessionController.connect()
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

    private fun moveConnectionActivity(item: ConnectionInfoItem?) {
        if (item == null) {
            startActivity(ConnectionActivity::class.java)
            finishAllActivities()
            return
        }

        connectionInfoController.setAutoConnectionFlag(item.id, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                if (error != null || data == null) {
                    return@subscribe
                }

                startActivity(ConnectionActivity::class.java)
                finishAllActivities()
            }.addTo(compositeDisposable)
    }
}