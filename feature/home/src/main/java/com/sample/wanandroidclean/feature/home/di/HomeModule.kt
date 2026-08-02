package com.sample.wanandroidclean.feature.home.di

import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.GetArticlesPagingUseCase
import com.sample.wanandroidclean.domain.usecase.ToggleCollectUseCase
import com.sample.wanandroidclean.feature.home.articles.ArticlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    // 显式声明所有注入类型，彻底解决 Kotlin 2.0 编译器下的类型推断失败问题
    viewModel { 
        ArticlesViewModel(
            getArticlesPagingUseCase = get<GetArticlesPagingUseCase>(), 
            bannerRepository = get<BannerRepository>(), 
            toggleCollectUseCase = get<ToggleCollectUseCase>(), 
            userRepository = get<UserRepository>()
        ) 
    }
}
