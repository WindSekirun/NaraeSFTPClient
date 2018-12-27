package com.github.windsekirun.naraesftp.progress

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.windsekirun.baseapp.base.BaseBottomSheetDialogFragment
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.naraesftp.R
import com.github.windsekirun.naraesftp.databinding.ConfirmDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ConfirmDialog : BaseBottomSheetDialogFragment<ConfirmDialogBinding>() {

    val message = ObservableString()
    lateinit var callback: () -> Unit
    lateinit var closeCallback: () -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeLight)

    override fun createView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) =
        ConfirmDialogBinding.inflate(layoutInflater, viewGroup, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialog = this
    }

    fun clickClose(view: View) {
        if (::closeCallback.isInitialized) {
            closeCallback.invoke()
        }
        dismiss()
    }

    fun clickConfirm(view: View) {
        if (::callback.isInitialized) {
            callback.invoke()
        }

        dismiss()
    }

    companion object {
        fun show(activity: AppCompatActivity, message: String, callback: () -> Unit) {
            val fragment = ConfirmDialog().apply {
                this.message.set(message)
                this.callback = callback
            }

            activity.supportFragmentManager.beginTransaction()
                .add(fragment, "confirm").commit()
        }
    }
}