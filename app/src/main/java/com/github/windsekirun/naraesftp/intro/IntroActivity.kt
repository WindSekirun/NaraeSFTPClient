package com.github.windsekirun.naraesftp.intro

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.IntroActivityBinding
import com.github.windsekirun.naraesftp.event.CloseProgressIndicatorDialog
import com.github.windsekirun.naraesftp.event.OpenProgressIndicatorDialog
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorDialog
import org.greenrobot.eventbus.Subscribe

/**
 * NaraeSFTPClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-12-27.
 *
 *
 * Description:
 */

@InjectActivity
class IntroActivity : BaseActivity<IntroActivityBinding>() {
    lateinit var mViewModel: IntroViewModel
    private var progressIndicatorDialog: ProgressIndicatorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)
        mViewModel = getViewModel(IntroViewModel::class.java)
        mBinding.viewModel = mViewModel
    }

    @Subscribe
    fun onProgressIndicatorDialog(event: OpenProgressIndicatorDialog) {
        progressIndicatorDialog = ProgressIndicatorDialog.show(this, event.message)
    }

    @Subscribe
    fun onCloseProgressIndicatorDialog(event: CloseProgressIndicatorDialog) {
        progressIndicatorDialog?.dismiss()
    }
}