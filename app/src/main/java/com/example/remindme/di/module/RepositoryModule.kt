package com.example.remindme.di.module

import com.example.remindme.data.database.reminder.ReminderDatabase
import com.example.remindme.data.database.reminder.repository.RoomReminderRepository
import com.example.remindme.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides a singleton instance of [RoomReminderRepository].
     */
    @Provides
    @Singleton
    fun provideReminderRepository(reminderDatabase: ReminderDatabase): ReminderRepository {
        return RoomReminderRepository(reminderDatabase.reminderDao())
    }

}