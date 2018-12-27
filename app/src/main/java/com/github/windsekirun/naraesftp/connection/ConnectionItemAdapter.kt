package com.github.windsekirun.naraesftp.connection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.databinding.ConnectionItemBinding
import com.github.windsekirun.naraesftp.event.ClickConnectionItemEvent

/**
 * NaraeSFTPClient
 * Class: ConnectionItemAdapter
 * Created by Pyxis on 2018-12-28.
 *
 *
 * Description:
 */
class ConnectionItemAdapter : BaseRecyclerAdapter<ConnectionInfoItem, ConnectionItemBinding>() {

    override fun bind(binding: ConnectionItemBinding, item: ConnectionInfoItem, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: ConnectionItemBinding, item: ConnectionInfoItem, position: Int) {
        postEvent(ClickConnectionItemEvent(item))
    }

    override fun onLongClickedItem(binding: ConnectionItemBinding, item: ConnectionInfoItem, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, R.layout.connection_item, parent, false)
    }
}