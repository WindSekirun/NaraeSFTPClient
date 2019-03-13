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
import com.github.windsekirun.naraesftp.databinding.ProgressIndicatorDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProgressIndicatorDialog : BaseBottomSheetDialogFragment<ProgressIndicatorDialogBinding>() {
    val message = ObservableString()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeLight)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        ProgressIndicatorDialogBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialog = this
    }

    companion object {
        fun show(activity: AppCompatActivity, message: String): ProgressIndicatorDialog {
            val fragment = ProgressIndicatorDialog().apply {
                this.message.set(message)
            }

            activity.supportFragmentManager.beginTransaction()
                .add(fragment, "progress-indicator").commit()

            return fragment
        }
    }
}