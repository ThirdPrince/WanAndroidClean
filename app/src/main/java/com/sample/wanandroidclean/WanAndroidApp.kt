package com.sample.wanandroidclean

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.sample.wanandroidclean.data.di.dataModule
import com.sample.wanandroidclean.domain.di.domainModule
import com.sample.wanandroidclean.feature.home.di.homeModule
import com.sample.wanandroidclean.feature.mine.di.mineModule
import com.sample.wanandroidclean.feature.project.di.projectModule
import com.sample.wanandroidclean.feature.system.di.systemModule
import com.sample.wanandroidclean.feature.wxarticle.di.wxArticleModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * 玩Android 应用入口。
 * 实现 [ImageLoaderFactory] 是 Coil 2.x 推荐的全局配置方式，
 * 能够确保全应用（包括 AsyncImage）都使用我们自定义的离线缓存加载器。
 */
class WanAndroidApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WanAndroidApp)
            modules(
                dataModule, 
                domainModule, 
                homeModule, 
                systemModule, 
                wxArticleModule, 
                projectModule, 
                mineModule
            )
        }
    }

    // 实现此方法，Coil 内部会自动调用它来获取 ImageLoader
    override fun newImageLoader(): ImageLoader {
        return get()
    }
}
