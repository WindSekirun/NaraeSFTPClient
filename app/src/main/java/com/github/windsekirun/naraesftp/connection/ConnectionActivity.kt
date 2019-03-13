package com.github.windsekirun.naraesftp.connection

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.ConnectionActivityBinding
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.progress.ConfirmDialog
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
class ConnectionActivity : BaseActivity<ConnectionActivityBinding>() {
    lateinit var viewModel: ConnectionViewModel

    private var progressIndicatorDialog: ProgressIndicatorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connection_activity)
        viewModel = getViewModel(ConnectionViewModel::class.java)
        mBinding.viewModel = viewModel

        initRecyclerView<ConnectionItemAdapter>(mBinding.recyclerView, ConnectionItemAdapter::class.java)
    }

    @Subscribe
    fun onOpenConnectionAddDialog(event: OpenConnectionAddDialog) {
        ConnectionAddDialog.show(this, event.callback)
    }

    @Subscribe
    fun onOpenConnectionEditDialog(event: OpenConnectionEditDialog) {
        ConnectionEditDialog.show(this, event.item, event.callback)
    }

    @Subscribe
    fun onClickConnectionItemEvent(event: ClickConnectionItemEvent) {
        viewModel.tryConnection(event.item)
    }

    @Subscribe
    fun onProgressIndicatorDialog(event: OpenProgressIndicatorDialog) {
        progressIndicatorDialog = ProgressIndicatorDialog.show(this, event.message)
    }

    @Subscribe
    fun onCloseProgressIndicatorDialog(event: CloseProgressIndicatorDialog) {
        progressIndicatorDialog?.dismiss()
    }

    @Subscribe
    fun onOpenConfirmDialog(event: OpenConfirmDialog) {
        ConfirmDialog.show(this, event.message, event.callback, event.closeCallback)
    }

    @Subscribe
    fun onClickLongConnectionItemEvent(event: ClickLongConnectionItemEvent) {
        viewModel.clickConnectionLong(event.item)
    }
}