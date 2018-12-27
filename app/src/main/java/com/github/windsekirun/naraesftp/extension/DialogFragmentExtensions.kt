package com.github.windsekirun.naraesftp.extension

import androidx.fragment.app.DialogFragment

/**
 * NaraeSFTPClient
 * Class: DialogFragmentExtensions
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */

fun DialogFragment.checkFragmentNotOpenState() =
    this.dialog == null || this.dialog?.isShowing == false