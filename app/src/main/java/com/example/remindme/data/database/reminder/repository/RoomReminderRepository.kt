package com.example.remindme.data.database.reminder.repository

import android.util.Log
import com.example.remindme.data.database.reminder.ReminderDatabase
import com.example.remindme.data.database.reminder.dao.ReminderDao
import com.example.remindme.data.database.reminder.entity.ReminderEntity
import com.example.remindme.data.database.reminder.entity.toEntity
import com.example.remindme.domain.model.Reminder
import com.example.remindme.domain.repository.ReminderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A [ReminderRepository] that uses a [ReminderDao] to store [Reminder]s as [ReminderEntity]s in the
 * [ReminderDatabase].
 */
class RoomReminderRepository(private val reminderDao: ReminderDao) : ReminderRepository {

    /**
     * Creates a [Reminder] in the repository using [ReminderDao.insert]. Sets the [Reminder.id] to
     * 0 to allow Room to generate the ID.
     */
    override suspend fun createReminder(reminder: Reminder): Int {
        return reminderDao.insert(reminder.copy(id = 0).toEntity()).toInt()
    }

    /**
     * Creates a [Flow] of a single [Reminder] by its ID, may be null if the [Reminder] does not
     * exist. Converts the [ReminderEntity] to a [Reminder].
     */
    override fun getReminderStream(id: Int): Flow<Reminder?> {
        return reminderDao.getReminderFlow(id).map { it?.toDomain() }
    }

    /**
     * Creates a [Flow] of all [Reminder]s, converting each [ReminderEntity] to a [Reminder].
     */
    override fun getRemindersStream(): Flow<List<Reminder>> {
        return reminderDao.getRemindersFlow().map { reminders ->
            reminders.map { it.toDomain() }
        }
    }

    /**
     * Deletes a [Reminder] from the repository using [ReminderDao.delete].
     */
    override suspend fun deleteReminder(id: Int) = reminderDao.delete(id)
}