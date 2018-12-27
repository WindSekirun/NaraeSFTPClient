package com.github.windsekirun.naraesftp.file

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.FileListActivityBinding

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
    lateinit var mViewModel: FileListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_list_activity)
        mViewModel = getViewModel(FileListViewModel::class.java)
        mBinding.setViewModel(mViewModel)
    }
}