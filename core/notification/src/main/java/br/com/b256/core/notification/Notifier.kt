package br.com.b256.core.notification

import br.com.b256.core.model.Notification


interface Notifier {
    fun notify(value: List<Notification>)
}
