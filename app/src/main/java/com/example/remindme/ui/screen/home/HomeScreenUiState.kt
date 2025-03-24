package com.example.remindme.ui.screen.home

import com.example.remindme.domain.model.Reminder

/**
 * Plain state holder for the [HomeScreen].
 */
data class HomeScreenUiState(
    /**
     * Stores all [Reminder]s to be displayed in the [HomeScreen].
     */
    val reminders: List<Reminder> = emptyList(),
)
