package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.usecase.*
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
    factory { LoginUseCase(get()) }
    factory { GetCollectionsUseCase(get()) }
    factory { GetSystemArticlesUseCase(get()) }
    factory { GetArticlesPagingUseCase(get()) } // Add this line
}
