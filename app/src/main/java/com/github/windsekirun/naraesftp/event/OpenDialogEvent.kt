package com.github.windsekirun.naraesftp.event

import com.github.windsekirun.naraesftp.data.ConnectionInfoItem

/**
 * Event class for open ConnectionAddDialog
 */
class OpenConnectionAddDialog(val callback: (ConnectionInfoItem) -> Unit)

/**
 * Event class for open ProgressIndicatorDialog
 */
class OpenProgressIndicatorDialog(val message: String)

/**
 * Event class for open ConfirmDialog
 */
class OpenConfirmDialog constructor(val message: String, val closeCallback: (() -> Unit)? = null, val callback: () -> Unit)