package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.GetHomeScreenDataUseCase
import com.sample.wanandroidclean.domain.usecase.GetNavigationUseCase
import com.sample.wanandroidclean.domain.usecase.GetProjectArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetProjectChaptersUseCase
import com.sample.wanandroidclean.domain.usecase.GetSystemCategoriesUseCase
import com.sample.wanandroidclean.domain.usecase.GetUserInfoUseCase
import com.sample.wanandroidclean.domain.usecase.GetWxArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetWxChaptersUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetHomeScreenDataUseCase(get(), get(), get()) }
    factory { GetSystemCategoriesUseCase(get()) }
    factory { GetNavigationUseCase(get()) }
    factory { GetWxChaptersUseCase(get()) }
    factory { GetWxArticlesUseCase(get()) }
    factory { GetProjectChaptersUseCase(get()) }
    factory { GetProjectArticlesUseCase(get()) }
    factory { GetUserInfoUseCase(get()) }
}
