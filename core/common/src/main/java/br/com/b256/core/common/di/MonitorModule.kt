package br.com.b256.core.common.di

import br.com.b256.core.common.monitor.ManagerNetworkMonitor
import br.com.b256.core.common.monitor.ManagerTimeZoneMonitor
import br.com.b256.core.common.monitor.NetworkMonitor
import br.com.b256.core.common.monitor.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitorModule {
    @Binds
    internal abstract fun bindsTimeZoneMonitor(
        managerTimeZoneMonitor: ManagerTimeZoneMonitor,
    ): TimeZoneMonitor

    @Binds
    internal abstract fun bindsNetworkMonitor(
        managerNetworkMonitor: ManagerNetworkMonitor,
    ): NetworkMonitor
}
