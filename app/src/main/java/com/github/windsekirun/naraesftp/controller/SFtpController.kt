package com.github.windsekirun.naraesftp.controller

import android.text.TextUtils
import com.github.windsekirun.naraesftp.data.ProgressMonitorItem
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpProgressMonitor
import io.reactivex.Observable
import java.io.File
import java.io.IOException
import java.util.*

/**
 * NaraeSFTPClient
 * Class: SFtpController
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */
class SFtpController {

    /**
     * Remote directory path
     */
    var currentPath: String = "/"

    /**
     * Reset [currentPath] to root
     */
    fun resetPathToRoot() {
        currentPath = "/"
    }

    /**
     * Appends [path] to the current path on remote host
     */
    fun appendToPath(path: String) {
        currentPath += path
    }

    /**
     * upload [localFile] to [Session] into [currentPath]
     *
     * it publish [ProgressMonitorItem] to observe progress state in onNext(),
     * and publish onComplete() when upload is done.
     */
    fun uploadFile(session: Session, localFile: File): Observable<ProgressMonitorItem> {
        return Observable.create { emitter ->
            // request to connect if doesn't not connect.
            if (!session.isConnected) session.connect()

            val channelSftp = session.openSftpChannel()

            val monitor = object : SftpProgressMonitor {
                var count: Long = 0
                var size: Long = 0

                override fun count(count: Long): Boolean {
                    this.count += count
                    val progress = this.count.toFloat() / size.toFloat() * 100
                    emitter.onNext(ProgressMonitorItem(size, count, progress))
                    return true
                }

                override fun end() {
                    emitter.onNext(ProgressMonitorItem(size, count, 100.0f))
                    emitter.onComplete()
                }

                override fun init(op: Int, src: String?, dest: String?, max: Long) {
                    size = max
                }
            }

            channelSftp.put(localFile.path, localFile.name, monitor, ChannelSftp.APPEND)
        }
    }

    /**
     * get List of Remote files in [Session] by given [path]
     *
     * @throws IOException when channelSftp.ls is null.
     */
    fun getListRemoteFiles(session: Session, path: String, backward: Boolean = false): Observable<List<ChannelSftp.LsEntry>> {
        currentPath = if (TextUtils.isEmpty(path)) currentPath else if (backward) path else "$currentPath$path/"
        currentPath = currentPath.replace("//", "/")
        return Observable.create { emitter ->
            val channelSftp = session.openSftpChannel()
            val files: Vector<Any>? = channelSftp.ls(currentPath)
            channelSftp.disconnect()

            if (files == null) {
                emitter.onError(IOException("Cannot fetch items"))
            } else {
                // safe cast instead unchecked cast because type parameter of [files] is Any
                val list = files
                    .filterIsInstance(ChannelSftp.LsEntry::class.java)
                    .filter { it.filename != ".." && it.filename != "." }
                    .sortedBy { it.filename }
                    .toList()

                emitter.onNext(list)
            }
        }
    }

    /**
     * Download file in [Session] with given [srcPath] and [out]
     *
     * it publish [ProgressMonitorItem] to observe progress state in onNext(),
     * and publish onComplete() when upload is done.
     */
    fun downloadFile(session: Session, srcPath: String, out: String): Observable<ProgressMonitorItem> {
        return Observable.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)

            val monitor = object : SftpProgressMonitor {
                var count: Long = 0
                var size: Long = 0

                override fun count(count: Long): Boolean {
                    this.count += count
                    val progress = this.count.toFloat() / size.toFloat() * 100
                    emitter.onNext(ProgressMonitorItem(size, count, progress))
                    return true
                }

                override fun end() {
                    emitter.onNext(ProgressMonitorItem(size, count, 100.0f))
                    emitter.onComplete()
                }

                override fun init(op: Int, src: String?, dest: String?, max: Long) {
                    size = max
                }
            }

            channelSftp.get("$currentPath$srcPath", out, monitor, ChannelSftp.OVERWRITE)
        }
    }

    private fun Session.openSftpChannel(nonInput: Boolean = true): ChannelSftp =
        with(this.openChannel("sftp")) {
            if (nonInput) this.inputStream = null
            this.connect()
            this as ChannelSftp
        }

    companion object {
        private const val TAG = "SFtpController"
    }
}