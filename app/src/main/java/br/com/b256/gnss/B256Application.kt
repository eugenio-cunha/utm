package br.com.b256.gnss

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * `Application` do projeto. `@HiltAndroidApp` é o gatilho que faz o Hilt gerar o componente de DI
 * de nível de aplicação ([dagger.hilt.components.SingletonComponent]) — obrigatório existir
 * exatamente uma classe assim, mesmo sem lógica própria de inicialização.
 */
@HiltAndroidApp
class B256Application : Application()
