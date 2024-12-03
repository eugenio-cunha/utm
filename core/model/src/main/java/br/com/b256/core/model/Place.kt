package br.com.b256.core.model

import kotlinx.serialization.Serializable

import kotlinx.datetime.Instant

@Serializable
data class Place(
    val id: Int,
    val title: String?,
    val date: Instant,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
)
