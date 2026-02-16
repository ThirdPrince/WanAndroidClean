package com.sample.wanandroidclean.data.remote

/**
 * Represents an exception for business errors from the API.
 * @param code The business error code from the API.
 * @param message The business error message from the API.
 */
class ApiException(val code: Int, override val message: String) : Exception(message)

class NetworkException(
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)
