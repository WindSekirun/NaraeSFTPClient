package com.github.windsekirun.naraesftp.intro

import androidx.lifecycle.LifecycleOwner

import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.naraesftp.MainApplication
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

    override fun onCreate(owner: LifecycleOwner) {
        runDelayedOnUiThread({
            checkConnectionAvailable()
        }, 1000)
    }

    private fun checkConnectionAvailable() {

    }
}