package br.com.b256.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant


class InstantConverter {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        if (value == null) {
            return null
        }

        return value.toEpochMilliseconds()

    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        if (value == null) {
            return null
        }

        return Instant.fromEpochMilliseconds(value)
    }
}
