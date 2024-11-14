package br.com.b256.core.database.di

import android.content.Context
import androidx.room.Room
import br.com.b256.core.database.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    internal fun providesDatabaseModule(@ApplicationContext context: Context): RoomDatabase =
        Room.databaseBuilder(
            context = context,
            klass = RoomDatabase::class.java,
            name = "database.db",
        ).build()
}
