package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.GetHomeScreenDataUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetHomeScreenDataUseCase(get(), get(), get()) }
}
