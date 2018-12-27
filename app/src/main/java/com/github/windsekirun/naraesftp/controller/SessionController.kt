package com.github.windsekirun.naraesftp.controller

import android.annotation.SuppressLint
import android.util.Log
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pyxis.uzuki.live.richutilskt.impl.F1
import pyxis.uzuki.live.richutilskt.utils.runOnUiThread
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * NaraeSFTPClient
 * Class: SessionController
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */
class SessionController(val sFtpController: SFtpController) {
    lateinit var session: Session
    lateinit var connectionInfo: ConnectionInfoItem
    lateinit var sshDisposable: Disposable
    var callback: F1<Boolean>? = null

    /**
     * Connect to [Session]
     */
    fun connect() {
        if (!::session.isInitialized) {
            connectSession()
        } else if (!session.isConnected) {
            connectSession()
        }
    }

    /**
     * Disconnect [Session]
     */
    fun disconnect() {
        sshDisposable.dispose()
        callback?.invoke(false)
    }

    /**
     * check if session instance is connected
     */
    fun isConnected() = session.isConnected

    /**
     * upload [file] to [Session] into [SFtpController.currentPath]
     *
     * @see [SFtpController.uploadFile] to details.
     */
    fun uploadFile(file: File) = sFtpController.uploadFile(session, file)

    /**
     * download file in [Session] by given [srcPath] and [out]
     *
     * @see [SFtpController.downloadFile] to details.
     */
    fun downloadFile(srcPath: String, out: String) = sFtpController.downloadFile(session, srcPath, out)

    /**
     * get List of Remote files in [Session] by given [path]
     *
     *  @see [SFtpController.getListRemoteFiles] to details.
     */
    fun getListRemoteFiles(path: String) = sFtpController.getListRemoteFiles(session, path)

    @SuppressLint("CheckResult")
    private fun connectSession() {
        sshDisposable = Observable.create<Boolean> {
            val jsch = JSch()
            val properties = Properties().apply { setProperty("StrictHostKeyChecking", "no") }
            session = jsch.getSession(connectionInfo.user, connectionInfo.host, connectionInfo.port).apply {
                userInfo = connectionInfo
                setConfig(properties)
                connect()
            }

            it.onNext(true)
        }.flatMap { Observable.interval(2, TimeUnit.SECONDS) }
            .flatMap { Observable.just(session.isConnected) }
            .subscribe { data, throwable ->
                Log.e(TAG, "message: ${throwable?.message}", throwable)
                runOnUiThread { callback?.invoke(data) }
            }
    }

    companion object {
        private const val TAG = "SessionController"
    }
}