package com.sample.wanandroidclean.feature.project.di

import com.sample.wanandroidclean.feature.project.ProjectViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val projectModule = module {
    viewModel { ProjectViewModel(get()) }
}
