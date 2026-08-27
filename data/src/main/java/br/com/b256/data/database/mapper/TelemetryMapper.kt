package br.com.b256.data.database.mapper

import br.com.b256.data.database.entities.TelemetryEntity
import br.com.b256.domain.entities.Telemetry

/**
 * Conversão nos dois sentidos entre a entidade de banco ([TelemetryEntity]) e a de domínio
 * ([Telemetry]) — `asDomain()` ao ler do Room, `asEntity()` ao persistir. Mesma convenção usada
 * em `network/mapper/PingMapper.kt` para a camada de rede.
 */
internal fun TelemetryEntity.asDomain(): Telemetry =
    Telemetry(
        id = id,
        success = success,
        date = date,
    )

internal fun List<TelemetryEntity>.asDomain(): List<Telemetry> = map { it.asDomain() }

internal fun Telemetry.asEntity(): TelemetryEntity =
    TelemetryEntity(
        id = id,
        success = success,
        date = date,
    )

internal fun List<Telemetry>.asEntities(): List<TelemetryEntity> = map { it.asEntity() }
