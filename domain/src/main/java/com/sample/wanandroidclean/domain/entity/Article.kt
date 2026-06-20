package com.sample.wanandroidclean.domain.entity

/**
 * Represents an article in the domain layer.
 */
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val link: String,
    val isTop: Boolean = false,
    val collect: Boolean = false // Added to track collection status
)
