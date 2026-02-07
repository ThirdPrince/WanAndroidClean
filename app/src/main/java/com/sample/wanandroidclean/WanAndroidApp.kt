package com.sample.wanandroidclean

import android.app.Application
import com.sample.wanandroidclean.data.di.dataModule
import com.sample.wanandroidclean.domain.di.domainModule
import com.sample.wanandroidclean.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WanAndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WanAndroidApp)
            modules(dataModule, domainModule,presentationModule)
        }
    }
}
