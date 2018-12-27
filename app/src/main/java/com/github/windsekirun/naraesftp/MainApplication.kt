package com.github.windsekirun.naraesftp

import android.app.Activity
import android.app.Service
import android.content.Context
import com.github.windsekirun.baseapp.BaseApplication
import com.github.windsekirun.daggerautoinject.DaggerAutoInject
import com.github.windsekirun.daggerautoinject.InjectApplication
import com.github.windsekirun.naraesftp.data.MyObjectBox
import com.github.windsekirun.naraesftp.di.AppComponent
import com.github.windsekirun.naraesftp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.objectbox.Box
import io.objectbox.BoxStore
import pyxis.uzuki.live.attribute.parser.annotation.AttributeParser
import javax.inject.Inject

/**
 * PyxisBaseApp
 * Class: MainApplication
 * Created by Pyxis on 2018-01-22.
 *
 *
 * Description:
 */
@AttributeParser("com.github.windsekirun.naraesftp")
@InjectApplication(component = AppComponent::class)
class MainApplication : BaseApplication(), HasActivityInjector, HasServiceInjector {
    @Inject
    lateinit var mActivityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var mServiceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>
    private var mBoxStore: BoxStore? = null

    override val configFilePath: String
        get() = "config.json"

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        DaggerAutoInject.init(this, appComponent)

        mBoxStore = MyObjectBox.builder().androidContext(this).build()
    }

    fun <T> getBox(cls: Class<T>): Box<T> {
        return mBoxStore!!.boxFor(cls)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return mActivityDispatchingAndroidInjector
    }

    override fun serviceInjector(): AndroidInjector<Service>? {
        return mServiceDispatchingAndroidInjector
    }

    companion object {
        /**
         * @return [DaggerAppComponent] to inject
         */
        var appComponent: AppComponent? = null
            private set

        fun getApplication(context: Context): MainApplication {
            return if (context is BaseApplication) {
                context as MainApplication
            } else context.applicationContext as MainApplication

        }
    }
}
