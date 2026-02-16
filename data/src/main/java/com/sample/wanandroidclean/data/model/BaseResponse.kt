package com.sample.wanandroidclean.data.model

import kotlinx.serialization.Serializable

/**
 * A generic base response from the WanAndroid API.
 * @param <T> The type of the data.
 */
@Serializable
data class BaseResponse<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)
