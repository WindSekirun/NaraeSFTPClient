package com.github.windsekirun.naraesftp.binding

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

object BindAdapter {
    @JvmStatic
    @BindingAdapter("navigationItemClicked")
    fun bindNavigationItemClick(view: Toolbar, listener: View.OnClickListener) {
        view.setNavigationOnClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("actionSelected")
    fun bindActionSelectedToolbar(view: Toolbar, listener: Toolbar.OnMenuItemClickListener) {
        view.setOnMenuItemClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("title")
    fun setTitle(view: Toolbar, text: String) {
        view.title = text
    }

    @BindingAdapter("onEditorAction")
    @JvmStatic
    fun bindEditorAction(editText: EditText, onEditActionListener: TextView.OnEditorActionListener) {
        editText.setOnEditorActionListener(onEditActionListener)
    }
}