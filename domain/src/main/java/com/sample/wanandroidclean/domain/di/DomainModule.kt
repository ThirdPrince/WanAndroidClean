package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.GetArticlesUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetArticlesUseCase(get()) }
}
