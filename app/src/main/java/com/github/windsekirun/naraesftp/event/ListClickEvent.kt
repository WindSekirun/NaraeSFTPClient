package com.github.windsekirun.naraesftp.event

import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.ChannelSftp
import java.io.File

/**
 * Event class for click [ConnectionInfoItem]
 */
class ClickConnectionItemEvent(val item: ConnectionInfoItem)

/**
 * Event class for Longclick [ConnectionInfoItem]
 */
class ClickLongConnectionItemEvent(val item: ConnectionInfoItem)

/**
 * Event class for click [ChannelSftp.LsEntry]
 */
class ClickEntryItemEvent(val item: ChannelSftp.LsEntry)

/**
 * Event class for click [File]
 */
class ClickEntryFileEvent(val item: File)