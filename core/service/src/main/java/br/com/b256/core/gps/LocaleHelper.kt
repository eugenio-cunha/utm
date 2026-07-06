package br.com.b256.core.gps

import android.content.res.Resources
import android.view.View
import br.com.b256.core.model.Locales
import java.util.Locale

object LocaleHelper {

    private var appLocale = Locale.getDefault()

    fun getAppLocale(): Locale {
        return synchronized(this) {
            appLocale
        }
    }
}
