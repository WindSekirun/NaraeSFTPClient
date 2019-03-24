package com.github.windsekirun.naraesftp.controller

import android.annotation.SuppressLint
import android.util.Log
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
    fun getListRemoteFiles(path: String, backward: Boolean = false) =
        sFtpController.getListRemoteFiles(session, path, backward)

    /**
     * get Home directory of remote connection
     *
     * @see [SFtpController.getHomeDirectory] to details
     */
    fun getHomeDirectory() = sFtpController.getHomeDirectory(session)

    /**
     * create Directory into remote connection with given [name]
     *
     * @see [SFtpController.createDirectory] to details
     */
    fun createDirectory(name: String) = sFtpController.createDirectory(session, name)

    @SuppressLint("CheckResult")
    private fun connectSession() {
        sshDisposable = Observable
            .create<Boolean> {
                val jsch = JSch()
                val properties = Properties().apply { setProperty("StrictHostKeyChecking", "no") }
                session = jsch.getSession(connectionInfo.user, connectionInfo.host, connectionInfo.port).apply {
                    userInfo = connectionInfo
                    setConfig(properties)
                    connect()
                }

                it.onNext(true)
            }
            .flatMap { Observable.interval(2, TimeUnit.SECONDS) }
            .flatMap { Observable.just(session.isConnected) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, throwable ->
                if (data != null) {
                    runOnUiThread { callback?.invoke(data) }
                } else if (throwable != null) {
                    runOnUiThread { callback?.invoke(false) }
                    Log.e(TAG, "message: ${throwable.message}", throwable)
                }
            }
    }

    companion object {
        private const val TAG = "SessionController"
    }
}