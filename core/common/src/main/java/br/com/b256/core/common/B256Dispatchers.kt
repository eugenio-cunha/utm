package br.com.b256.core.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: B256Dispatchers)

enum class B256Dispatchers {
    Default,
    IO,
}
