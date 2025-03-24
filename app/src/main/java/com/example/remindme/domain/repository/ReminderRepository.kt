package com.example.remindme.domain.repository

import com.example.remindme.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing [Reminder]s.
 */
interface ReminderRepository {

    /**
     * Creates a [Reminder] in the repository returning the ID of the created [Reminder].
     */
    suspend fun createReminder(reminder: Reminder): Int

    /**
     * Creates a [Flow] of a single [Reminder] by its ID, may be null if the [Reminder] does not
     * exist.
     */
    fun getReminderStream(id: Int): Flow<Reminder?>

    /**
     * Creates a [Flow] of all [Reminder]s.
     */
    fun getRemindersStream(): Flow<List<Reminder>>

    /**
     * Deletes a [Reminder] from the repository.
     */
    suspend fun deleteReminder(id: Int)

}