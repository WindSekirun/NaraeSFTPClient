package com.github.windsekirun.naraesftp.intro

import androidx.lifecycle.LifecycleOwner

import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.file.FileListActivity
import io.objectbox.kotlin.query
import io.reactivex.rxkotlin.addTo
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

    lateinit var connectionInfoController: ConnectionInfoController

    override fun onCreate(owner: LifecycleOwner) {
        runDelayedOnUiThread({
            checkConnectionAvailable()
        }, 1000)
    }

    private fun checkConnectionAvailable() {
        connectionInfoController.findLastConnectionItem()
            .subscribe { t1, t2 ->
                if (t1 != null) {
                    connectionInfoController.setLastConnectionInfo(t1.id, true)
                    startActivity(FileListActivity::class.java)
                    finishAllActivities()
                    return@subscribe
                }


            }.addTo(compositeDisposable)
    }
}