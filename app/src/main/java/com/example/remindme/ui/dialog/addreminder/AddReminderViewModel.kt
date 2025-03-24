package com.example.remindme.ui.dialog.addreminder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.remindme.domain.model.Reminder
import com.example.remindme.domain.repository.ReminderRepository
import com.example.remindme.domain.repository.ReminderWorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * Handles the UI state for the [AddReminderDialog].
 */
@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderWorkRepository: ReminderWorkRepository
) : ViewModel() {
    /**
     * Internal [AddReminderUiState].
     */
    private val _uiState = MutableStateFlow(AddReminderUiState())

    /**
     * Exposes [AddReminderUiState] as a [StateFlow].
     */
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

    /**
     * Holds the text value of the title field.
     */
    var title by mutableStateOf("")
        private set

    /**
     * Holds the text value of the description field.
     */
    var description by mutableStateOf("")
        private set

    /**
     * Validates the [title] and updates the UI state accordingly.
     */
    fun validate(): Boolean {
        val isValidTile = validateTitle(title)
        val isValidReminderTime = _uiState.value.selectedReminderTime != null

        _uiState.update {
            it.copy(
                showTitleError = !isValidTile,
                showReminderTimeError = !isValidReminderTime,
            )
        }

        return isValidTile && isValidReminderTime
    }

    /**
     * Creates a [Reminder] using [ReminderRepository.createReminder] and schedules it using
     * [ReminderWorkRepository.scheduleReminder].
     */
    suspend fun createReminder(): Boolean {
        val reminderTime = _uiState.value.selectedReminderTime
        val timeZone = TimeZone.currentSystemDefault()

        // Verify that reminderTime is not null
        if (reminderTime == null) {
            validate()
            return false
        }

        // Calculate the date and time for the reminder
        val dateTime = Clock.System.now()
            .plus(reminderTime.count, reminderTime.unit, timeZone)
            .toLocalDateTime(timeZone)

        // Create reminder in repository
        val id = reminderRepository.createReminder(
            Reminder(
                id = 0,
                title = title,
                description = description,
                dateTime = dateTime
            )
        )
        // Schedule reminder in work repository
        reminderWorkRepository.scheduleReminder(
            Reminder(
                id = id,
                title = title,
                description = description,
                dateTime = dateTime
            )
        )

        return true;
    }

    /**
     * Updates the [title] to [input].
     */
    fun updateTitle(input: String) {
        title = input

        if (_uiState.value.showTitleError)
            _uiState.update { it.copy(showTitleError = false) }
    }

    /**
     * Updates the [description] to [input].
     */
    fun updateDescription(input: String) {
        description = input
    }

    /**
     * Updates the [AddReminderUiState.selectedReminderTime] to [reminderTime].
     */
    fun updateReminderTime(reminderTime: ReminderTime) {
        _uiState.update {
            it.copy(
                selectedReminderTime = reminderTime,
                showReminderTimeError = false,
            )
        }
    }

    /**
     * Set UI state to locked or unlocked.
     */
    fun lockUi(lock: Boolean) {
        _uiState.update { it.copy(lockUi = lock) }
    }

    companion object {
        /**
         * Validates the [title]. Title cannot be blank.
         */
        private fun validateTitle(title: String): Boolean = title.isNotBlank()
    }
}