package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.BaseResponse
import retrofit2.HttpException
import java.io.IOException

/**
 * A safe wrapper for making API calls that checks for HTTP and business errors.
 *
 * @param T The type of the data inside the BaseResponse.
 * @param apiCall The suspend function that makes the network request and returns a Retrofit Response.
 * @return A [Result] object, either [Result.Success] with the data or [Result.Failure] with an exception.
 */
suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> BaseResponse<T>
): Result<T> {
    return try {
        val response = apiCall()

        if (response.errorCode == 0) {
            response.data?.let {
                Result.success(it)
            } ?: Result.failure(
                ApiException(-1, "Response data is null")
            )
        } else {
            Result.failure(
                ApiException(response.errorCode, response.errorMsg)
            )
        }

    } catch (e: IOException) {
        Result.failure(NetworkException("Network error", e))

    } catch (e: HttpException) {
        Result.failure(NetworkException("HTTP ${e.code()}", e))

    } catch (e: Exception) {
        Result.failure(e)
    }
}

