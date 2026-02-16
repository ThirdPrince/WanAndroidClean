package com.sample.wanandroidclean.data.model

import kotlinx.serialization.Serializable


    @Serializable
    data class ArticleData(
        val curPage: Int,
        val datas: List<ArticleDto>,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
    )

