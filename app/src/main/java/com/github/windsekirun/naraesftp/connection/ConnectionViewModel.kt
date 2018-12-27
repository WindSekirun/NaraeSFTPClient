package com.github.windsekirun.naraesftp.connection

import android.view.MenuItem
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem

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

    @Inject lateinit var connectionInfoController: ConnectionInfoController

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadData()
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        return true
    }

    private fun loadData() {

    }
}