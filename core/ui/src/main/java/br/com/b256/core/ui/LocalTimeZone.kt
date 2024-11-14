package br.com.b256.core.ui

import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.TimeZone

/**
 * TimeZone que pode ser fornecido com o TimeZoneMonitor. Dessa forma, não é necessário
 * passar para cada composable o fuso horário para mostrar na UI.
 */
val LocalTimeZone = compositionLocalOf { TimeZone.currentSystemDefault() }
