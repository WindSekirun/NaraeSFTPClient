package com.github.windsekirun.naraesftp.file

import androidx.lifecycle.LifecycleOwner
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication

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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

    }
}