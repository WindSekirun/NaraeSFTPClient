package com.github.windsekirun.naraesftp.connection

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import com.github.windsekirun.baseapp.base.BaseBottomSheetDialogFragment
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.databinding.ConnectionEditDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import pyxis.uzuki.live.richutilskt.utils.safeInt

class ConnectionEditDialog : BaseBottomSheetDialogFragment<ConnectionEditDialogBinding>() {

    val title = ObservableString()
    val host = ObservableString()
    val user = ObservableString()
    val password = ObservableString()
    val port = ObservableString("22")
    val directory = ObservableString()
    val item = ObservableField<ConnectionInfoItem>()

    lateinit var callback: (Int, ConnectionInfoItem) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeLight)

    override fun createView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) =
        ConnectionEditDialogBinding.inflate(layoutInflater, viewGroup, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialog = this
    }

    fun clickClose(view: View) {
        dismiss()
    }

    fun clickRemove(view: View) {
        if (::callback.isInitialized) {
            callback.invoke(-1, item.get() ?: ConnectionInfoItem())
        }

        dismiss()
    }

    fun clickConfirm(view: View) {
        val item = (item.get() ?: ConnectionInfoItem()).apply {
            this.title = this@ConnectionEditDialog.title.get()
            this.host = this@ConnectionEditDialog.host.get()
            this.user = this@ConnectionEditDialog.user.get()
            this.pw = this@ConnectionEditDialog.password.get()
            this.port = this@ConnectionEditDialog.port.get().safeInt()
            this.initialDirectory = (this@ConnectionEditDialog.directory.get())
        }

        if (::callback.isInitialized) {
            callback.invoke(1, item)
        }

        dismiss()
    }

    companion object {
        fun show(activity: AppCompatActivity, item: ConnectionInfoItem, callback: (Int, ConnectionInfoItem) -> Unit) {
            val fragment = ConnectionEditDialog().apply {
                this.callback = callback
                this.title.set(item.title)
                this.host.set(item.host)
                this.user.set(item.user)
                this.password.set(item.password)
                this.item.set(item)
                this.directory.set(item.initialDirectory ?: "/")
            }

            activity.supportFragmentManager.beginTransaction()
                .add(fragment, "connection-edit").commit()
        }
    }
}