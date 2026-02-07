package com.sample.wanandroidclean.presentation.di

import com.sample.wanandroidclean.presentation.articles.ArticlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { ArticlesViewModel(get()) }
}
