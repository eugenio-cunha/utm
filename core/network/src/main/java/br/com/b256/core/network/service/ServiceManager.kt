package br.com.b256.core.network.service

import androidx.tracing.trace
import br.com.b256.core.common.Resource
import br.com.b256.core.model.Pong
import br.com.b256.core.network.BuildConfig
import br.com.b256.core.network.api.Api
import br.com.b256.core.network.model.asModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceManager @Inject constructor(
    json: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : Service {
    private val api = trace("RetrofitB256Network") {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            // Usamos lambda callFactory aqui com dagger.Lazy<Call.Factory>
            // para evitar a inicialização do OkHttp no thread principal.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(Api::class.java)
    }

    override suspend fun ping(): Flow<Resource<Pong>> = flow {
        try {
            emit(Resource.Loading)
            emit(Resource.Success(api.ping().body()!!.asModel()))
        } catch (e: HttpException) {
            emit(Resource.Error(e))
        } catch (e: IOException) {
            emit(Resource.Error(e))
        }
    }
}
