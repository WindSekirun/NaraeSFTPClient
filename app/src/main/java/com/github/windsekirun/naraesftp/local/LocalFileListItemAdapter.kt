package com.github.windsekirun.naraesftp.local

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
import java.io.File

/**
 * NaraeSFTPClient
 * Class: FileListItemAdapter
 * Created by Pyxis on 2018-12-28.
 *
 *
 * Description:
 */
class LocalFileListItemAdapter : BaseRecyclerAdapter<File, com.github.windsekirun.naraesftp.databinding.LocalFileListItemBinding>(),
    FastScrollRecyclerView.SectionedAdapter {

    override fun bind(binding: LocalFileListItemBinding, item: File, position: Int) {
        binding.item = item
    }

    override fun onClickedItem(binding: LocalFileListItemBinding, item: File, position: Int) {

    }

    override fun onLongClickedItem(binding: LocalFileListItemBinding, item: File, position: Int): Boolean {
        return false
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, R.layout.local_file_list_item, parent, false)
    }

    override fun getSectionName(position: Int): String {
        return mItemList[position].name.substring(0, 1).toUpperCase()
    }
}