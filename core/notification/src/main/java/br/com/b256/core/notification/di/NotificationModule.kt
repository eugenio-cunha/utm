package br.com.b256.core.notification.di

import br.com.b256.core.notification.Notifier
import br.com.b256.core.notification.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationModule {
    @Binds
    abstract fun bindsNotifier(notifier: SystemTrayNotifier): Notifier
}
