package br.com.b256.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

/**
 * Modelo de linha da tabela `telemetry` (formato de banco), convertido para a entidade de
 * domínio [Telemetry] pelo mapper em `database/mapper/TelemetryMapper.kt`.
 */
@Entity(
    tableName = "telemetry",
)
internal data class TelemetryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "success")
    val success: Boolean,
    @ColumnInfo(name = "date")
    val date: Instant,
)
