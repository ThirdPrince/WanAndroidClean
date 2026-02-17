package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.GetHomeScreenDataUseCase
import com.sample.wanandroidclean.domain.usecase.GetSystemCategoriesUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetHomeScreenDataUseCase(get(), get(), get()) }
    factory { GetSystemCategoriesUseCase(get()) }
}
