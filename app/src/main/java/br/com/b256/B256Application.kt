package br.com.b256

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class B256Application : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
