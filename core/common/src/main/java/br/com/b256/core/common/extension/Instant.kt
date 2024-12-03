package br.com.b256.core.common.extension

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.localDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        .format(formatter)
}
