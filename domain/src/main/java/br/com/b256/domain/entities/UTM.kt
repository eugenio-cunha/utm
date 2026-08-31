package br.com.b256.domain.entities

data class UTM(
    /**
     * Zone of the UTM coordinates
     */
    val zone: String,

    /**
     * Easting of the UTM coordinates
     */
    val easting: String,

    /**
     * Northing of the UTM coordinates
     */
    val northing: String,

    /**
     * [centralMeridian] returns the meridian of the
     * given coordinates
     */
    val centralMeridian: String
)
