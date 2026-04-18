package com.sample.wanandroidclean.feature.system.di

import com.sample.wanandroidclean.feature.system.SystemDetailViewModel
import com.sample.wanandroidclean.feature.system.SystemViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val systemModule = module {
    viewModel { SystemViewModel(get(), get()) }
    viewModel { SystemDetailViewModel(get()) }
}
