package com.github.windsekirun.naraesftp.data

import com.jcraft.jsch.UserInfo
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import pyxis.uzuki.live.richutilskt.utils.asDateString
import java.util.*

/**
 * NaraeSFTPClient
 * Class: ConnectionInfoItem
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */
@Entity
class ConnectionInfoItem : UserInfo {
    @Id
    var id: Long = 0
    var host: String = ""
    var user: String = ""
    var pw: String = ""
    var title: String = ""
    var port: Int = 0

    var initialDirectory: String = "/"
    var lastConnectionTime: Date = Date()
    var autoConnect: Boolean = false

    override fun promptPassphrase(message: String?): Boolean {
        return true
    }

    override fun getPassphrase(): String {
        return ""
    }

    override fun getPassword(): String {
        return pw
    }

    override fun promptYesNo(message: String?): Boolean {
        return true
    }

    override fun showMessage(message: String?) {

    }

    override fun promptPassword(message: String?): Boolean {
        return true
    }

    fun getHostPort() = "$host:$port"

    fun getLastConnectionDate() = lastConnectionTime.asDateString("yyyy-MM-dd")
}