package com.github.windsekirun.naraesftp.local

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.controller.SessionController
import com.jcraft.jsch.ChannelSftp
import java.io.File
import javax.inject.Inject

@InjectViewModel
class LocalFileListViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {
    val entries = ObservableArrayList<File>()
    val path = ObservableString()
    val filterEnable = ObservableBoolean()
    val hasData = ObservableBoolean()

    @Inject lateinit var sessionController: SessionController

    fun loadData() {

    }
}