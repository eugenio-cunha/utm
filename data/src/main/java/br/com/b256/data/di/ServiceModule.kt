package br.com.b256.data.di

import android.content.Context
import br.com.b256.data.services.location.LocationProviderImpl
import br.com.b256.domain.interfaces.LocationProvider
import br.com.b256.domain.interfaces.SettingsRepository
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
    fun providesLocationProvider(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
    ): LocationProvider =
        LocationProviderImpl(context = context, settingsRepository = settingsRepository)
}
