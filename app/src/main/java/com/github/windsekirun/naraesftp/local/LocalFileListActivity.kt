package com.github.windsekirun.naraesftp.local

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.FileListActivityBinding
import com.github.windsekirun.naraesftp.event.*
import com.github.windsekirun.naraesftp.file.FileListItemAdapter
import com.github.windsekirun.naraesftp.progress.ConfirmDialog
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorDialog
import com.github.windsekirun.naraesftp.progress.ProgressIndicatorPercentDialog
import com.github.windsekirun.naraesftp.view.SheetFab
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
class LocalFileListActivity : BaseActivity<com.github.windsekirun.naraesftp.databinding.LocalFileListActivityBinding>() {
    lateinit var viewModel: LocalFileListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_list_activity)
        viewModel = getViewModel(LocalFileListViewModel::class.java)
        mBinding.viewModel = viewModel

        initRecyclerView<LocalFileListItemAdapter>(mBinding.recyclerView, LocalFileListItemAdapter::class.java)
    }

}