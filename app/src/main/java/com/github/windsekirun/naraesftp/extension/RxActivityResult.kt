package com.github.windsekirun.naraesftp.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.windsekirun.baseapp.impl.NonActivityInterface
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables
import pyxis.uzuki.live.richutilskt.utils.toMap


object RxActivityResult : NonActivityInterface {
    private var doneCallback: ((ActivityResult) -> Unit)? = null

    @JvmStatic
    fun result(): Single<ActivityResult> = RxActivityResultObservable()

    @JvmStatic
    @JvmOverloads
    fun startActivityForResult(
        intent: Intent,
        activity: AppCompatActivity? = null, requestCode: Int = 72
    ) {
        val targetActivity: AppCompatActivity?

        targetActivity = if (activity == null) {
            val cachedActivity = requireActivity()
            if (cachedActivity is AppCompatActivity) cachedActivity else null
        } else {
            activity
        }

        if (targetActivity == null) {
            throw IllegalStateException("Cannot fetch AppCompatActivity by ActivityReference")
        }

        requestFragment(targetActivity, intent, requestCode)
    }

    private fun requestFragment(activity: AppCompatActivity, intent: Intent, requestCode: Int) {
        val fragmentManager = activity.supportFragmentManager

        val fragment = ResultFragment(fragmentManager) {
            doneCallback?.invoke(it)
        }

        fragmentManager.beginTransaction()
            .add(fragment, "FRAGMENT_TAG")
            .commitAllowingStateLoss()

        fragmentManager.executePendingTransactions()
        fragment.startActivityForResult(intent, requestCode)
    }

    class ActivityResult {
        var requestCode: Int = 0
        var resultCode: Int = 0
        var data: Intent? = null

        fun <K, V> toMapString(map: Map<K, V>, delimiter: CharSequence = "\n"): String {
            val builder = StringBuilder()
            val lists = map.entries.toList()
            for (item in lists) builder.append("[${item.key}] -> [${item.value}]$delimiter")
            return builder.toString()
        }

        override fun toString(): String {
            var dataStr = ""
            if (data != null && data?.extras != null) {
                dataStr = toMapString(data?.extras?.toMap() ?: hashMapOf(), "\n")
            }

            return "ActivityResult(requestCode=$requestCode, resultCode=$resultCode, data=$dataStr)"
        }
    }

    class ResultFragment() : Fragment() {
        var manager: FragmentManager? = null
        var callback: ((ActivityResult) -> Unit)? = null

        @SuppressLint("ValidFragment")
        constructor(manager: FragmentManager, callback: (ActivityResult) -> Unit) : this() {
            this.manager = manager
            this.callback = callback
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (manager == null || callback == null) return

            val activityResult = ActivityResult().apply {
                this.requestCode = requestCode
                this.resultCode = resultCode
                this.data = data
            }

            callback?.invoke(activityResult)

            manager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    class RxActivityResultObservable : Single<ActivityResult>() {

        override fun subscribeActual(observer: SingleObserver<in ActivityResult>) {
            if (!checkMainThread(observer)) return

            val listener = ResultListener(observer)

            // assign
            doneCallback = listener
            observer.onSubscribe(listener)
        }

        class ResultListener(private val observer: SingleObserver<in ActivityResult>?) :
            MainThreadDisposable(), (ActivityResult) -> Unit {
            override fun invoke(result: ActivityResult) {
                observer?.onSuccess(result)
            }

            override fun onDispose() {

            }
        }

        companion object {

            fun <T> checkMainThread(observer: SingleObserver<T>): Boolean {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    observer.onSubscribe(Disposables.empty())
                    observer.onError(
                        IllegalStateException(
                            "Expected to be called on the main thread but was " + Thread.currentThread().name
                        )
                    )
                    return false
                }
                return true
            }
        }
    }
}