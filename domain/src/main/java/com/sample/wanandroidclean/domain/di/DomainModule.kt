package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.GetArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetBannersUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetArticlesUseCase(get()) }
    factory { GetBannersUseCase(get()) }
}
