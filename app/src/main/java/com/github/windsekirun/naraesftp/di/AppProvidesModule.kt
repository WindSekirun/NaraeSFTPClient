package com.github.windsekirun.naraesftp.di

import com.github.windsekirun.naraesftp.MainApplication
import dagger.Module
import dagger.Provides
import pyxis.uzuki.live.richutilskt.utils.RPreference

import javax.inject.Singleton

@Module
class AppProvidesModule {

    @Provides
    @Singleton
    fun provideRPerference(application: MainApplication): RPreference {
        return RPreference.getInstance(application)
    }
}