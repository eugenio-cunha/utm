package br.com.b256.core.gps.di

import android.content.Context
import br.com.b256.core.gps.LocationProvider
import br.com.b256.core.gps.LocationProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesLocationProvider(@ApplicationContext context: Context): LocationProvider =
        LocationProviderImpl(context = context)
}
