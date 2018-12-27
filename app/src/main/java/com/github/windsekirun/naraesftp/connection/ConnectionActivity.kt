package com.github.windsekirun.naraesftp.connection

import android.os.Bundle

import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.ConnectionActivityBinding

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
    lateinit var mViewModel: ConnectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connection_activity)
        mViewModel = getViewModel(ConnectionViewModel::class.java)
        mBinding.viewModel = mViewModel

        mBinding.toolbar.inflateMenu(R.menu.menu_connection)
//        initRecyclerView(mBinding.recyclerView, )
    }
}