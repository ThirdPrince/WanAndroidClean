package com.sample.wanandroidclean.feature.mine.di

import com.sample.wanandroidclean.feature.mine.CollectionViewModel
import com.sample.wanandroidclean.feature.mine.LoginViewModel
import com.sample.wanandroidclean.feature.mine.MineViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mineModule = module {
    viewModel { MineViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { CollectionViewModel(get()) }
}
