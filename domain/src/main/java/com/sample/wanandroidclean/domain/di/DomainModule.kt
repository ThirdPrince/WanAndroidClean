package com.sample.wanandroidclean.domain.di

import com.sample.wanandroidclean.domain.repository.*
import com.sample.wanandroidclean.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    // 使用显式类型声明声明，解决不同 Kotlin 编译器环境下的类型推断问题
    factory { GetSystemCategoriesUseCase(get<SystemRepository>()) }
    factory { GetNavigationUseCase(get<NavigationRepository>()) }
    factory { GetWxChaptersUseCase(get<WxArticleRepository>()) }
    factory { GetWxArticlesUseCase(get<WxArticleRepository>()) }
    factory { GetWxArticlesPagingUseCase(get<WxArticleRepository>()) }
    factory { GetProjectChaptersUseCase(get<ProjectRepository>()) }
    factory { GetProjectArticlesUseCase(get<ProjectRepository>()) }
    factory { GetUserInfoUseCase(get<UserInfoRepository>()) }
    factory { LoginUseCase(get<UserRepository>()) }
    factory { LogoutUseCase(get<UserRepository>()) } // 报错点：确保 LogoutUseCase 文件已提交
    factory { GetCollectionsUseCase(get<CollectionRepository>()) }
    factory { GetSystemArticlesUseCase(get<SystemRepository>()) }
    factory { GetArticlesPagingUseCase(get<ArticleRepository>()) }
    factory { GetBannersUseCase(get<BannerRepository>()) }
    
    factory { 
        ToggleCollectUseCase(
            collectionRepository = get<CollectionRepository>(), 
            articleRepository = get<ArticleRepository>()
        ) 
    }
}
