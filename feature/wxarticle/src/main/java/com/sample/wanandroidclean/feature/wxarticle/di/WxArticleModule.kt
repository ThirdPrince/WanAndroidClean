package com.sample.wanandroidclean.feature.wxarticle.di

import com.sample.wanandroidclean.feature.wxarticle.WxArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val wxArticleModule = module {
    viewModel { WxArticleViewModel(get(), get()) }
}
