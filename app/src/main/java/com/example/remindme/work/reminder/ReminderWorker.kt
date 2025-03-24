package com.example.remindme.work.reminder

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.remindme.notification.NotificationHelper
import com.example.remindme.notification.REMINDER_CHANNEL_ID
import com.example.remindme.notification.createReminderNotificationChannel
import com.example.remindme.work.reminder.WorkManagerReminderWorkRepository.InputKeys.DESCRIPTION_KEY
import com.example.remindme.work.reminder.WorkManagerReminderWorkRepository.InputKeys.ID_KEY
import com.example.remindme.work.reminder.WorkManagerReminderWorkRepository.InputKeys.TITLE_KEY

/**
 * Worker for creating reminder notifications.
 */
class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    /**
     * Creates a notification for a reminder.
     */
    override fun doWork(): Result {
        // Get input data
        val id = inputData.getInt(ID_KEY, -1)
        val title = inputData.getString(TITLE_KEY)
        val description = inputData.getString(DESCRIPTION_KEY)

        // Require title and description
        if (id < 0 || title == null || description == null) {
            Log.e(TAG, "Invalid input data: TITLE_KEY or DESCRIPTION_KEY is null")
            return Result.failure()
        }

        // Create notification channel and notification
        with(NotificationHelper) {
            // Create notification channel
            createReminderNotificationChannel(context)

            // Build notification
            val notification = createNotification(
                context = context,
                channelId = REMINDER_CHANNEL_ID,
                title = title,
                content = description,
                priority = NotificationCompat.PRIORITY_HIGH,
            )

            // Show notification
            return if (showNotification(context, id, notification))
                Result.success()
            else
                Result.failure()
        }

        // Should never reach here
        return Result.retry()
    }

    companion object {
        private val TAG = ReminderWorker.javaClass.simpleName
    }

}