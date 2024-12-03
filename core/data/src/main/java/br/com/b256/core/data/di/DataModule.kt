package br.com.b256.core.data.di

import br.com.b256.core.data.repository.NetworkRepository
import br.com.b256.core.data.repository.NetworkRepositoryImpl
import br.com.b256.core.data.repository.PlaceRepository
import br.com.b256.core.data.repository.PlaceRepositoryImpl
import br.com.b256.core.data.repository.SettingsRepository
import br.com.b256.core.data.repository.SettingsRepositoryImpl
import br.com.b256.core.database.RoomDatabase
import br.com.b256.core.datastore.Preference
import br.com.b256.core.network.service.Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    internal fun providesNetworkRepository(service: Service): NetworkRepository =
        NetworkRepositoryImpl(service = service)

    @Provides
    @Singleton
    internal fun providesSettingsRepository(preference: Preference): SettingsRepository =
        SettingsRepositoryImpl(preference = preference)

    @Provides
    @Singleton
    internal fun providesPlaceRepository(database: RoomDatabase): PlaceRepository =
        PlaceRepositoryImpl(database = database)
}
