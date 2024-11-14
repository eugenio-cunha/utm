package br.com.b256.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.b256.core.database.converter.InstantConverter
import br.com.b256.core.database.dao.SettingsDao
import br.com.b256.core.database.model.SettingsEntity

@Database(
    version = 1,
    entities = [SettingsEntity::class],
    autoMigrations = [],
    exportSchema = true,

    )
@TypeConverters(
    InstantConverter::class,
)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun settings(): SettingsDao
}
