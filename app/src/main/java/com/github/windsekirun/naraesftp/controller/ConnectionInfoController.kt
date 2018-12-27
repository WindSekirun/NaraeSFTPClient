package com.github.windsekirun.naraesftp.controller

import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem
import com.github.windsekirun.naraesftp.data.ConnectionInfoItem_
import io.objectbox.Box
import io.objectbox.kotlin.query
import io.objectbox.query.OrderFlags
import io.reactivex.Single
import java.util.*

/**
 * NaraeSFTPClient
 * Class: ConnectionInfoController
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */
class ConnectionInfoController(val application: MainApplication) {
    private val box: Box<ConnectionInfoItem> by lazy { application.getBox(ConnectionInfoItem::class.java) }

    /**
     * find last of [ConnectionInfoItem] which [ConnectionInfoItem.autoConnect] is true.
     *
     * @throws NullPointerException if data not found (Neither dataList is empty and no one has true value of autoConnect)
     */
    fun findLastConnectionItem(): Single<ConnectionInfoItem> {
        return Single.create { emitter ->
            val find = box.query {
                equal(ConnectionInfoItem_.autoConnect, true)
            }.find()

            if (find.isNotEmpty()) {
                emitter.onSuccess(find[0])
            } else {
                emitter.onError(NullPointerException("Not found"))
            }
        }
    }

    /**
     * add [ConnectionInfoItem] into box
     */
    fun addConnectionInfo(connectionInfoItem: ConnectionInfoItem): Single<Long> {
        return Single.create { emitter ->
            val id = box.put(connectionInfoItem.apply {
                lastConnectionTime = Date()
            })
            emitter.onSuccess(id)
        }
    }

    /**
     * get List of [ConnectionInfoItem]
     */
    fun getListConnectionInfo(): Single<List<ConnectionInfoItem>> {
        return Single.create { emitter ->
            val find = box.query {
                order(ConnectionInfoItem_.lastConnectionTime, OrderFlags.DESCENDING)
            }.find()

            emitter.onSuccess(find)
        }
    }

    /**
     * set Last, autoConnect information for [ConnectionInfoItem]
     */
    fun setLastConnectionInfo(id: Long, autoConnect: Boolean): Single<ConnectionInfoItem> {
        return setAutoConnectionFlag(id, autoConnect)
            .flatMap {
                val item = box.get(id).apply {
                    this.lastConnectionTime = Date()
                }

                box.put(item)
                Single.just(item)
            }
    }

    /**
     * set autoConnect information for [ConnectionInfoItem]
     */
    fun setAutoConnectionFlag(id: Long, autoConnect: Boolean): Single<Long> {
        return Single.create { emitter ->
            val list = box.all.map {
                it.autoConnect = false
                it
            }

            box.put(list)

            val item = box.get(id).apply {
                this.autoConnect = autoConnect
            }

            box.put(item)
            emitter.onSuccess(item.id)
        }
    }
}