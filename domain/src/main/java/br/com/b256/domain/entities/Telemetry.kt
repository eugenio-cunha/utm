package br.com.b256.domain.entities

import kotlin.time.Instant

data class Telemetry(
    val id: String,
    val success: Boolean,
    val date: Instant,
)
