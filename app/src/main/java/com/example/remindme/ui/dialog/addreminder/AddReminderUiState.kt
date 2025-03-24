package com.example.remindme.ui.dialog.addreminder

/**
 * Holds UI state for the [AddReminderDialog].
 */
data class AddReminderUiState(
    /**
     * Whether the UI should be locked.
     */
    val lockUi: Boolean = false,

    /**
     * The selected [ReminderTime].
     */
    val selectedReminderTime: ReminderTime? = null,

    /**
     * Whether the title field should show an error.
     */
    val showTitleError: Boolean = false,

    /**
     * Whether the reminder time field should show an error.
     */
    val showReminderTimeError: Boolean = false
)
