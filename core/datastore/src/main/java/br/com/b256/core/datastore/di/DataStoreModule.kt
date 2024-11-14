package br.com.b256.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import br.com.b256.core.datastore.Preference
import br.com.b256.core.datastore.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    internal fun providesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            migrations = listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = SHARED_PREFERENCES_NAME,
                ),
            ),
            produceFile = { context.preferencesDataStoreFile(SHARED_PREFERENCES_NAME) },
        )

    @Provides
    @Singleton
    internal fun providesPreference(dataStore: DataStore<Preferences>): Preference {
        return PreferenceManager(dataStore = dataStore)
    }

    private const val SHARED_PREFERENCES_NAME = "user_preferences"
}
