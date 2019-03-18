package com.github.windsekirun.naraesftp.event

import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.ChannelSftp
import java.io.File

/**
 * Event class for open ConnectionAddDialog
 */
class OpenConnectionAddDialog(val callback: (ConnectionInfoItem) -> Unit)

/**
 * Event class for open ConnectionEditDialog
 */
class OpenConnectionEditDialog(
    val item: ConnectionInfoItem,
    val callback: (Int, ConnectionInfoItem) -> Unit
)

/**
 * Event class for open ProgressIndicatorDialog
 */
class OpenProgressIndicatorDialog(val message: String, val mode: Int = 0)

/**
 * Event class for close ProgressIndicatorDialog
 */
class CloseProgressIndicatorDialog(val mode: Int = 0)

/**
 * Event class for open ConfirmDialog
 */
class OpenConfirmDialog constructor(
    val message: String,
    val mode: Int = 0,
    val closeCallback: (() -> Unit)? = null,
    val callback: () -> Unit
)

/**
 * Event class for open ProgressIndicatorPercentDialog
 */
class OpenProgressIndicatorPercentDialog(val message: String, val item: ChannelSftp.LsEntry)

/**
 * Event class for open ProgressIndicatorPercentDialog
 */
class OpenProgressIndicatorPercentFileDialog(val message: String, val item: File)