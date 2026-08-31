package br.com.b256.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.b256.data.database.dao.TelemetryDao
import br.com.b256.data.database.entities.TelemetryEntity
import br.com.b256.data.database.util.InstantConverter

/**
 * Banco Room do `:data`. Instanciado como singleton em `data/di/DatabaseModule.kt`.
 *
 * Ao adicionar uma nova [androidx.room.Entity]: inclua a classe em `entities`, exponha o
 * respectivo `@Dao` aqui (como [telemetryDao]) e **incremente `version`**, adicionando uma
 * `AutoMigration` em `autoMigrations` (ou uma `Migration` manual) — o schema exportado
 * (`exportSchema = true`) fica em `data/schemas` e é o que valida migrações em tempo de build.
 */
@Database(
    version = 1,
    entities = [TelemetryEntity::class],
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class RoomDatabase() : RoomDatabase() {
    abstract fun telemetryDao(): TelemetryDao
}
