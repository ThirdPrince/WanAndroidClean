package com.sample.wanandroidclean.feature.mine.di

import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.*
import com.sample.wanandroidclean.feature.mine.CollectionViewModel
import com.sample.wanandroidclean.feature.mine.LoginViewModel
import com.sample.wanandroidclean.feature.mine.MineViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mineModule = module {
    // 补全依赖注入：显式指定类型以解决类型推断报错
    viewModel { 
        MineViewModel(
            getUserInfoUseCase = get<GetUserInfoUseCase>(),
            logoutUseCase = get<LogoutUseCase>(),
            userRepository = get<UserRepository>()
        )
    }

    viewModel { LoginViewModel(get<LoginUseCase>()) }

    viewModel { 
        CollectionViewModel(
            getCollectionsUseCase = get<GetCollectionsUseCase>(),
            toggleCollectUseCase = get<ToggleCollectUseCase>()
        )
    }
}
