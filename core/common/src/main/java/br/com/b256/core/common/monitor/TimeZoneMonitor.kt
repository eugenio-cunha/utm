package br.com.b256.core.common.monitor

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone

interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}
