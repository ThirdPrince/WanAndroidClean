package com.sample.wanandroidclean.feature.home.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.usecase.GetArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetBannersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getBannersUseCase: GetBannersUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners = _banners.asStateFlow()

    init {
        loadArticles()
        loadBanners()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            getArticlesUseCase().onSuccess {
                _articles.value = it

            }
        }
    }

    private fun loadBanners() {
        viewModelScope.launch {
            getBannersUseCase().onSuccess {
                _banners.value = it
            }
        }
    }
}
