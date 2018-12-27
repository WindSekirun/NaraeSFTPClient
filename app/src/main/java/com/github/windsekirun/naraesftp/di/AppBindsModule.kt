package com.github.windsekirun.naraesftp.di

import android.app.Application
import com.github.windsekirun.naraesftp.MainApplication
import dagger.Binds
import dagger.Module

@Module
abstract class AppBindsModule {

    @Binds
    abstract fun bindApplication(application: MainApplication): Application
}