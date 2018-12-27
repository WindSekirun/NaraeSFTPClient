package com.github.windsekirun.naraesftp.di

import com.github.windsekirun.naraesftp.MainApplication
import com.github.windsekirun.naraesftp.controller.ConnectionInfoController
import com.github.windsekirun.naraesftp.controller.SFtpController
import com.github.windsekirun.naraesftp.controller.SessionController
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * NaraeSFTPClient
 * Class: ControllerModule
 * Created by Pyxis on 2018-12-27.
 *
 * Description:
 */

@Module
class ControllerModule {

    @Provides
    @Singleton
    fun provideSFtpController(): SFtpController = SFtpController()

    @Provides
    @Singleton
    fun provideSessionController(sFtpController: SFtpController): SessionController = SessionController(sFtpController)

    @Provides
    @Singleton
    fun provideConnectionInfoController(application: MainApplication): ConnectionInfoController =
        ConnectionInfoController(application)
}