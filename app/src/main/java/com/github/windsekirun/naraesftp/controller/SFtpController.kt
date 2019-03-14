package com.github.windsekirun.naraesftp.controller

import android.text.TextUtils
import com.github.windsekirun.naraesftp.data.ProgressMonitorItem
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpProgressMonitor
import io.reactivex.Observable
import io.reactivex.Single
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
    fun getListRemoteFiles(
        session: Session,
        path: String,
        backward: Boolean = false
    ): Observable<List<ChannelSftp.LsEntry>> {
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

    /**
     * Create directory to remote path with given [name]
     */
    fun createDirectory(session: Session, name: String): Observable<String> {
        return Observable.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)
            channelSftp.mkdir("$currentPath/$name")
            channelSftp.disconnect()

            emitter.onNext("$currentPath/$name")
        }
    }

    /**
     * remove remote file with given [name]
     */
    fun removeFile(session: Session, name: String): Observable<String> {
        return Observable.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)
            channelSftp.rm("$currentPath/$name")
            channelSftp.disconnect()

            emitter.onNext("$currentPath/$name")
        }
    }

    /**
     * remove remote directory with given [name]
     */
    fun removeDirectory(session: Session, name: String): Observable<String> {
        return Observable.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)
            channelSftp.rmdir("$currentPath/$name")
            channelSftp.disconnect()

            emitter.onNext("$currentPath/$name")
        }
    }

    /**
     * rename remote file with given [name] to [change]
     */
    fun renameFile(session: Session, name: String, change: String): Observable<String> {
        return Observable.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)
            channelSftp.rename("$currentPath/$name", "$currentPath/$change")
            channelSftp.disconnect()

            emitter.onNext("$currentPath/$change")
        }
    }

    /**
     * get Home directory of remote connection
     */
    fun getHomeDirectory(session: Session): Single<String> {
        return Single.create { emitter ->
            if (!session.isConnected) session.connect()
            val channelSftp = session.openSftpChannel(false)
            val home = channelSftp.home
            channelSftp.disconnect()

            emitter.onSuccess(home)
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