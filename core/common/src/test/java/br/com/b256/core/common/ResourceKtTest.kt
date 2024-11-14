package br.com.b256.core.common

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ResourceKtTest {
    @Test
    fun result_catches_errors() = runTest {
        flow {
            emit(1)
            throw Exception("Test Done")
        }
            .asResult()
            .test {
                assertEquals(Resource.Loading, awaitItem())
                assertEquals(Resource.Success(1), awaitItem())

                when (val errorResult = awaitItem()) {
                    is Resource.Error -> assertEquals(
                        "Test Done",
                        errorResult.exception.message,
                    )

                    Resource.Loading,
                    is Resource.Success,
                        -> throw IllegalStateException(
                        "The flow should have emitted an Error Result",
                    )
                }

                awaitComplete()
            }
    }
}
