package com.example.remindme.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.remindme.MainActivity
import com.example.remindme.R

/**
 * Helper class for creating and showing notifications.
 */
object NotificationHelper {
    private const val TAG = "NotificationHelper"

    /**
     * Creates a [NotificationChannel] and registers it with the [NotificationManager]. Utility for
     * creating notification channel with string resources.
     */
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        @StringRes name: Int,
        @StringRes description: Int,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ) = createNotificationChannel(
        context,
        channelId,
        context.getString(name),
        context.getString(description),
        importance,
    )

    /**
     * Creates a [NotificationChannel] and registers it with the [NotificationManager].
     */
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        name: String,
        description: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ) {
        // Create notification channel
        val channel = NotificationChannel(channelId, name, importance).apply {
            this.description = description
        }

        // Register with NotificationManager
        getNotificationManager(context).createNotificationChannel(channel)
    }

    /**
     * Creates a [Notification] with the given parameters.
     */
    fun createNotification(
        context: Context,
        channelId: String,
        title: String,
        content: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    ): Notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(priority)
        .setContentIntent(createPendingIntent(context))
        .setAutoCancel(true)
        .build()

    /**
     * Shows a notification.
     */
    fun showNotification(context: Context, id: Int, notification: Notification): Boolean {
        // Verify notification permission
        if (!checkNotificationPermission(context)) {
            Log.e(TAG, "Notification permission not granted")
            return false
        }

        // Show notification
        NotificationManagerCompat.from(context).notify(id, notification)
        return true
    }

    /**
     * Creates a [PendingIntent] to launch the [MainActivity] when the notification is clicked.
     */
    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Checks if the notification permission is granted.
     */
    private fun checkNotificationPermission(context: Context): Boolean {
        return getNotificationManager(context).areNotificationsEnabled()
    }

    /**
     * Gets the [NotificationManager] from the [Context].
     */
    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}