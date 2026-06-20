package com.sample.wanandroidclean.feature.project.di

import com.sample.wanandroidclean.feature.project.ProjectViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val projectModule = module {
    // 补全构造函数参数：现在需要 4 个依赖
    viewModel { ProjectViewModel(get(), get(), get(), get()) }
}
