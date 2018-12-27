package com.github.windsekirun.naraesftp.ftp

import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.Session


/**
 * NaraeSFTPClient
 * Class: SessionController
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */
object SessionController {
    lateinit var session: Session
    lateinit var connectionInfo: ConnectionInfoItem


    const val TAG = "SessionController"
}