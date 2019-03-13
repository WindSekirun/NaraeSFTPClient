package com.github.windsekirun.naraesftp.event

import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.ChannelSftp

/**
 * Event class for open ConnectionAddDialog
 */
class OpenConnectionAddDialog(val callback: (ConnectionInfoItem) -> Unit)

/**
 * Event class for open ConnectionEditDialog
 */
class OpenConnectionEditDialog(val item: ConnectionInfoItem, val callback: (Int, ConnectionInfoItem) -> Unit)

/**
 * Event class for open ProgressIndicatorDialog
 */
class OpenProgressIndicatorDialog(val message: String)

/**
 * Event class for close ProgressIndicatorDialog
 */
class CloseProgressIndicatorDialog

/**
 * Event class for open ConfirmDialog
 */
class OpenConfirmDialog constructor(
    val message: String,
    val closeCallback: (() -> Unit)? = null,
    val callback: () -> Unit
)

/**
 * Event class for open ProgressIndicatorPercentDialog
 */
class OpenProgressIndicatorPercentDialog(val message: String, val item: ChannelSftp.LsEntry)