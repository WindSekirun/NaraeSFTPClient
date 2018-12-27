package com.github.windsekirun.naraesftp.file

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.FileListActivityBinding
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
class FileListActivity : BaseActivity<FileListActivityBinding>() {
    lateinit var viewModel: FileListViewModel
    private var progressIndicatorDialog: ProgressIndicatorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_list_activity)
        viewModel = getViewModel(FileListViewModel::class.java)
        mBinding.viewModel = viewModel

        initRecyclerView(mBinding.recyclerView, FileListItemAdapter::class.java)
        mBinding.toolbar.inflateMenu(R.menu.menu_filelist)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
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
    fun onClickEntryItemEvent(event: ClickEntryItemEvent) {
        viewModel.clickEntry(event.item)
    }

    @Subscribe
    fun onOpenProgressIndicatorPercentDialog(event: OpenProgressIndicatorPercentDialog) {
        val dialog = ProgressIndicatorPercentDialog.show(this, event.message)
        viewModel.startDownload(dialog, event.item)
    }

    @Subscribe
    fun onScrollUpEvent(event: ScrollUpEvent) {
        mBinding.recyclerView.post {
            val linearLayoutManager = mBinding.recyclerView.layoutManager as LinearLayoutManager
            linearLayoutManager.scrollToPosition(0)
        }
    }
}