package com.sample.wanandroidclean.feature.wxarticle.di

import com.sample.wanandroidclean.feature.wxarticle.WxArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val wxArticleModule = module {
    // 补全依赖注入：现在需要 4 个参数
    viewModel { WxArticleViewModel(get(), get(), get(), get()) }
}
