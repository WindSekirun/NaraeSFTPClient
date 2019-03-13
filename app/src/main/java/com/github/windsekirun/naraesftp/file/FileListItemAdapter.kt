package com.github.windsekirun.naraesftp.file

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.github.windsekirun.baseapp.module.recycler.BaseRecyclerAdapter
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.FileListItemBinding
import com.github.windsekirun.naraesftp.event.ClickEntryItemEvent
import com.jcraft.jsch.ChannelSftp
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

/**
 * NaraeSFTPClient
 * Class: FileListItemAdapter
 * Created by Pyxis on 2018-12-28.
 *
 *
 * Description:
 */
class FileListItemAdapter : BaseRecyclerAdapter<ChannelSftp.LsEntry, FileListItemBinding>(),
    FastScrollRecyclerView.SectionedAdapter {

    override fun bind(binding: FileListItemBinding, item: ChannelSftp.LsEntry, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: FileListItemBinding, item: ChannelSftp.LsEntry, position: Int) {
        postEvent(ClickEntryItemEvent(item))
    }

    override fun onLongClickedItem(binding: FileListItemBinding, item: ChannelSftp.LsEntry, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, R.layout.file_list_item, parent, false)
    }

    override fun getSectionName(position: Int): String {
        return mItemList[position].filename.substring(0, 1).toUpperCase()
    }
}