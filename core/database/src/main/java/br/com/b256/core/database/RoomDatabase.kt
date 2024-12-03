package br.com.b256.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.b256.core.database.converter.InstantConverter
import br.com.b256.core.database.dao.PlaceDao
import br.com.b256.core.database.dao.SettingsDao
import br.com.b256.core.database.model.PlaceEntity
import br.com.b256.core.database.model.SettingsEntity

@Database(
    version = 2,
    entities = [
        PlaceEntity::class,
        SettingsEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun settings(): SettingsDao
    abstract fun place(): PlaceDao
}
