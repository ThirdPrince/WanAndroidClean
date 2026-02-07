package com.sample.wanandroidclean.domain.entity

/**
 * Represents an article in the domain layer.
 */
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val link: String
)
