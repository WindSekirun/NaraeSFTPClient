package com.github.windsekirun.naraesftp.local

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.LocalFileListActivityBinding
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.progress.ConfirmDialog
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorDialog
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
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
class LocalFileListActivity : BaseActivity<LocalFileListActivityBinding>() {
    lateinit var viewModel: LocalFileListViewModel
    private var progressIndicatorDialog: ProgressIndicatorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_file_list_activity)
        viewModel = getViewModel(LocalFileListViewModel::class.java)
        mBinding.viewModel = viewModel

        initRecyclerView<LocalFileListItemAdapter>(mBinding.recyclerView, LocalFileListItemAdapter::class.java)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    @Subscribe
    fun onProgressIndicatorDialog(event: OpenProgressIndicatorDialog) {
        if (event.mode == 1) {
            progressIndicatorDialog = ProgressIndicatorDialog.show(this, event.message)
        }
    }

    @Subscribe
    fun onCloseProgressIndicatorDialog(event: CloseProgressIndicatorDialog) {
        if (event.mode == 1) {
            progressIndicatorDialog?.dismiss()
        }
    }

    @Subscribe
    fun onScrollUpEvent(event: ScrollUpEvent) {
        if (event.mode == 1) {
            mBinding.recyclerView.post {
                val linearLayoutManager = mBinding.recyclerView.layoutManager as LinearLayoutManager
                linearLayoutManager.scrollToPosition(0)
            }
        }
    }

    @Subscribe
    fun onOpenConfirmDialog(event: OpenConfirmDialog) {
        if (event.mode == 1) {
            ConfirmDialog.show(this, event.message, event.callback, event.closeCallback)
        }
    }

    @Subscribe
    fun onClickEntryFileEvent(event: ClickEntryFileEvent) {
        viewModel.clickEntry(event.item)
    }

    @Subscribe
    fun onOpenProgressIndicatorPercentFileDialog(event: OpenProgressIndicatorPercentFileDialog) {
        val dialog = ProgressIndicatorPercentDialog.show(this, event.message)
        viewModel.startUpload(dialog, event.item)
    }

}