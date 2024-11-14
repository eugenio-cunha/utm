package br.com.b256.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val exception: Throwable) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Resource<T>> = map<T, Resource<T>> { Resource.Success(it) }
    .onStart { emit(Resource.Loading) }
    .catch { emit(Resource.Error(it)) }
