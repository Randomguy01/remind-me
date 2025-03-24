package com.example.remindme.notification

import android.content.Context
import com.example.remindme.R

/**
 * ID of the notification channel for reminders.
 */
const val REMINDER_CHANNEL_ID = "reminder_channel"

/**
 * Creates the [NotificationChannel] for reminders.
 */
fun createReminderNotificationChannel(context: Context) {
    NotificationHelper.createNotificationChannel(
        context,
        REMINDER_CHANNEL_ID,
        R.string.notificationChannel_reminder_name,
        R.string.notificationChannel_reminder_description,
    )
}