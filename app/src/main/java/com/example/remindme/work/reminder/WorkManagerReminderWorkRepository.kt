package com.example.remindme.work.reminder

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.remindme.domain.model.Reminder
import com.example.remindme.domain.repository.ReminderWorkRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Manages scheduling of work for [Reminder]s using the [ReminderWorker] and the [WorkManager] API.
 */
class WorkManagerReminderWorkRepository(context: Context) : ReminderWorkRepository {
    /**
     * Instance of [WorkManager].
     */
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules a [ReminderWorker] for [reminder].
     */
    override fun scheduleReminder(reminder: Reminder) {
        // Create InputData
        val inputData = workDataOf(
            InputKeys.ID_KEY to reminder.id,
            InputKeys.TITLE_KEY to reminder.title,
            InputKeys.DESCRIPTION_KEY to reminder.description,
        )

        // Create OneTimeWorkRequest
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .addTag(getWorkTag(reminder.id))
            .setInitialDelay(calculateInitialDelay(reminder.dateTime).toJavaDuration())
            .build()

        // Enqueue work with WorkManager
        workManager.enqueue(reminderWorkRequest)
    }

    /**
     * Cancels all work, by tag, for the [Reminder] with id [reminderId].
     */
    override fun cancelReminder(reminderId: Int) {
        workManager.cancelAllWorkByTag(getWorkTag(reminderId))
    }

    /**
     * Input keys for [ReminderWorker].
     */
    object InputKeys {
        /**
         * Key for the [Reminder.id].
         */
        internal const val ID_KEY = "id"

        /**
         * Key for the [Reminder.title].
         */
        internal const val TITLE_KEY = "title"

        /**
         * Key for the [Reminder.description].
         */
        internal const val DESCRIPTION_KEY = "description"
    }

    companion object {
        /**
         * Calculates the initial delay for a [ReminderWorker].
         */
        private fun calculateInitialDelay(reminderTime: LocalDateTime): Duration {
            return reminderTime.toInstant(TimeZone.currentSystemDefault()).minus(Clock.System.now())
        }

        /**
         * Creates a tag for work done for a [Reminder].
         */
        private fun getWorkTag(reminderId: Int): String = "reminder_$reminderId"
    }
}