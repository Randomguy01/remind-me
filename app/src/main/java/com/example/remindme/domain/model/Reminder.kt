package com.example.remindme.domain.model

import kotlinx.datetime.LocalDateTime


/**
 * Represents an item to be reminded about.
 */
data class Reminder(
    /**
     * The ID of the [Reminder].
     */
    val id: Int,

    /**
     * The title of the [Reminder].
     */
    val title: String,

    /**
     * Description of the [Reminder].
     */
    val description: String,

    /**
     * The date and time of the [Reminder].
     */
    val dateTime: LocalDateTime,
)
