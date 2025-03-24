package com.example.remindme.data.database.reminder.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * Converts between [Instant] and [Long].
 */
class InstantConverter {

    /**
     * Converts a [Long] to a [Instant] using [Instant.fromEpochMilliseconds].
     */
    @TypeConverter
    fun fromTimestamp(value: Long): Instant? = Instant.fromEpochMilliseconds(value)

    /**
     * Converts a [Instant] to a [Long] using [Instant.toEpochMilliseconds].
     */
    @TypeConverter
    fun localDateTimeToTimestamp(instant: Instant): Long = instant.toEpochMilliseconds()

}