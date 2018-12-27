package com.github.windsekirun.naraesftp.data

/**
 * NaraeSFTPClient
 * Class: ProgressMonitorItem
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */

data class ProgressMonitorItem(val size: Long, val count: Long, val progress: Float) {

    val PUT = 0
    val GET = 1
    val UNKNOWN_SIZE = -1L
}