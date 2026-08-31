package br.com.b256.data.services.location

import java.util.Locale

internal object LocaleHelper {

    private var appLocale = Locale.getDefault()

    fun getAppLocale(): Locale {
        return synchronized(this) {
            appLocale
        }
    }
}
