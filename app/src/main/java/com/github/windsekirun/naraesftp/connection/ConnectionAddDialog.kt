package com.github.windsekirun.naraesftp.connection

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.windsekirun.baseapp.base.BaseBottomSheetDialogFragment
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.databinding.ConnectionAddDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import pyxis.uzuki.live.richutilskt.utils.safeInt

class ConnectionAddDialog : BaseBottomSheetDialogFragment<ConnectionAddDialogBinding>() {

    val directory = ObservableString("/")
    val host = ObservableString()
    val user = ObservableString()
    val password = ObservableString()
    val port = ObservableString("22")

    lateinit var callback: (ConnectionInfoItem) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeLight)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        ConnectionAddDialogBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialog = this
    }

    fun clickClose(view: View) {
        dismiss()
    }

    fun clickConfirm(view: View) {
        val item = ConnectionInfoItem().apply {
            this.host = this@ConnectionAddDialog.host.get()
            this.title = this.host
            this.user = this@ConnectionAddDialog.user.get()
            this.pw = this@ConnectionAddDialog.password.get()
            this.port = this@ConnectionAddDialog.port.get().safeInt()
            this.initialDirectory = this@ConnectionAddDialog.directory.get()
        }

        if (::callback.isInitialized) {
            callback.invoke(item)
        }

        dismiss()
    }

    companion object {
        fun show(activity: AppCompatActivity, callback: (ConnectionInfoItem) -> Unit) {
            val fragment = ConnectionAddDialog().apply {
                this.callback = callback
            }

            activity.supportFragmentManager.beginTransaction()
                .add(fragment, "connection-add").commit()
        }
    }
}