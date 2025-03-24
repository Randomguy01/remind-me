package com.example.remindme.domain.repository

import com.example.remindme.domain.model.Reminder

/**
 * Repository for performing [Reminder] work to notify the user of a reminder.
 */
interface ReminderWorkRepository {

    /**
     * Schedules the [reminder].
     */
    fun scheduleReminder(reminder: Reminder)

    /**
     * Cancels the reminder with [reminderId].
     */
    fun cancelReminder(reminderId: Int)
}