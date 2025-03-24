package com.example.remindme.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindme.domain.model.Reminder
import com.example.remindme.domain.repository.ReminderRepository
import com.example.remindme.domain.repository.ReminderWorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles business logic for the [HomeScreen].
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderWorkRepository: ReminderWorkRepository,
) : ViewModel() {

    /**
     * Creates a [HomeScreenUiState] from the [ReminderRepository.getRemindersStream].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = reminderRepository.getRemindersStream()
        .mapLatest {
            HomeScreenUiState(reminders = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenUiState(),
        )

    /**
     * Deletes [reminder] from the [reminderRepository].
     */
    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        runCatching {
            reminderRepository.deleteReminder(reminder.id)
        }.onSuccess {
            reminderWorkRepository.cancelReminder(reminder.id)
        }
    }

}