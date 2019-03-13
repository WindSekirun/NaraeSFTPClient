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
import com.github.windsekirun.naraesftp.databinding.ProgressIndicatorPercentDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProgressIndicatorPercentDialog : BaseBottomSheetDialogFragment<ProgressIndicatorPercentDialogBinding>() {
    val message = ObservableString()
    val percent = ObservableString()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThemeLight)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        ProgressIndicatorPercentDialogBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialog = this
    }

    fun setPercent(percent: Int) {
        this.percent.set("$percent%")
    }

    companion object {
        fun show(activity: AppCompatActivity, message: String): ProgressIndicatorPercentDialog {
            val fragment = ProgressIndicatorPercentDialog().apply {
                this.message.set(message)
                setPercent(0)
            }

            activity.supportFragmentManager.beginTransaction()
                .add(fragment, "progress-indicator-percent").commit()

            return fragment
        }
    }
}