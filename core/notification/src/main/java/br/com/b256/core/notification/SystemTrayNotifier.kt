package br.com.b256.core.notification

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import br.com.b256.core.model.Notification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.app.Notification as AndroidNotification

private const val MAX_NUM_NOTIFICATIONS = 5
private const val NOTIFICATION_SUMMARY_ID = 1
private const val NOTIFICATION_REQUEST_CODE = 0
private const val NOTIFICATION_CHANNEL_ID = "B256"
private const val NOTIFICATION_GROUP = "B256_NOTIFICATIONS"
private const val TARGET_ACTIVITY_NAME = "br.com.b256.MainActivity"

@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    override fun notify(value: List<Notification>) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        val truncated = value.take(MAX_NUM_NOTIFICATIONS)
        val notifications = truncated.map {
            createNotification {
                setSmallIcon(R.drawable.core_notification_icon)
                    .setContentTitle(it.title)
                    .setContentText(it.content)
                    .setContentIntent(pendingIntent(it))
                    .setGroup(NOTIFICATION_GROUP)
                    .setAutoCancel(true)
            }
        }

        val summaryNotification = createNotification {
            val title = getString(
                R.string.core_notification_group_summary,
                truncated.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notification_icon)
                // Crie informações resumidas no modelo InboxStyle.
                .setStyle(notificationStyle(title = title, value = truncated))
                .setGroup(NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncated[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Cria uma notificação de resumo no estilo caixa de entrada para atualizações de notícias
     * */
    private fun notificationStyle(
        title: String,
        value: List<Notification>,
    ): NotificationCompat.InboxStyle {
        return value.fold(NotificationCompat.InboxStyle()) { inboxStyle, notification ->
            inboxStyle.addLine(notification.title)
                .setBigContentTitle(title)
                .setSummaryText(title)
        }
    }
}

/**
 * Cria uma notificação
 */
private fun Context.createNotification(block: NotificationCompat.Builder.() -> Unit): AndroidNotification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Garante que um canal de notificação esteja presente, se aplicável
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notification_channel_description)
    }
    // Registre o canal no Android
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.pendingIntent(
    notification: Notification,
): PendingIntent? = PendingIntent.getActivity(
    this,
    NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = notification.deepLink.toUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)
