package com.example.remindme.data.database.reminder.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.remindme.data.database.reminder.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Manages database interactions with the [ReminderEntity].
 */
@Dao
interface ReminderDao {

    /**
     * Inserts a [ReminderEntity] into the database.
     */
    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    /**
     * Creates a [Flow] of a single [ReminderEntity] by its ID, may be null if the [ReminderEntity]
     * does not exist.
     */
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderFlow(id: Int): Flow<ReminderEntity?>

    /**
     * Creates a [Flow] of all [ReminderEntity]s.
     */
    @Query("SELECT * FROM reminders")
    fun getRemindersFlow(): Flow<List<ReminderEntity>>

    /**
     * Deletes a [ReminderEntity] from the database, [ReminderEntity.id] must be set correctly and
     * is the only value checked before deletion.
     */
    @Query("DELETE FROM reminders WHERE :id = id")
    suspend fun delete(id: Int)

}