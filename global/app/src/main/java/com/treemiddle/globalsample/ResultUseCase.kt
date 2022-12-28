package com.treemiddle.globalsample

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ResultUseCase<P, R>(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            withContext(dispatcher) {
                execute(params).let {
                    Result.Success(data = it)
                }
            }
        } catch (e: Exception) {
            Result.Error(exception = e)
        }
    }

    protected abstract suspend fun execute(params: P): R
}

sealed class Result<out R> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
