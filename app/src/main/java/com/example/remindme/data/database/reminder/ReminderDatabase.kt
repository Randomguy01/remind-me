package com.example.remindme.data.database.reminder

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.remindme.data.database.reminder.converter.InstantConverter
import com.example.remindme.data.database.reminder.dao.ReminderDao
import com.example.remindme.data.database.reminder.entity.ReminderEntity
import com.example.remindme.domain.model.Reminder

/**
 * A [RoomDatabase] for storing [Reminder]s.
 */
@Database(
    entities = [
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(InstantConverter::class)
abstract class ReminderDatabase : RoomDatabase() {

    /**
     * Creates a [ReminderDao] for managing [ReminderEntity]s.
     */
    abstract fun reminderDao(): ReminderDao

}