package br.com.b256.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import br.com.b256.data.database.entities.TelemetryEntity

/** DAO de referência: consultas em `suspend fun`, retornando entidades de banco ([TelemetryEntity]), nunca entidades de domínio. */
@Dao
internal interface TelemetryDao {
    @Query(value = "SELECT * FROM telemetry;")
    suspend fun getAll(): List<TelemetryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(entity: TelemetryEntity)

    @Query(value = "DELETE FROM telemetry WHERE id = :id;")
    suspend fun deleteTelemetryById(id: String)
}
