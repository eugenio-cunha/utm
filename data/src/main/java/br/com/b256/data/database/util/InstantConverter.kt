package br.com.b256.data.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * `@TypeConverter` do Room para persistir [kotlinx.datetime.Instant] como `Long` (epoch millis).
 * Registrado via `@TypeConverters(InstantConverter::class)` em [RoomDatabase].
 */
internal class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}
