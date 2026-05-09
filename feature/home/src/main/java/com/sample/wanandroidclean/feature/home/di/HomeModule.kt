package com.sample.wanandroidclean.feature.home.di

import com.sample.wanandroidclean.feature.home.articles.ArticlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { ArticlesViewModel(get(), get()) }
}
