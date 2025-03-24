package com.example.remindme.di.module

import android.content.Context
import com.example.remindme.domain.repository.ReminderWorkRepository
import com.example.remindme.work.reminder.WorkManagerReminderWorkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for work repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkRepository {

    /**
     * Provides a singleton instance of [WorkManagerReminderWorkRepository].
     */
    @Provides
    @Singleton
    fun provideReminderWorkRepository(@ApplicationContext context: Context): ReminderWorkRepository {
        return WorkManagerReminderWorkRepository(context)
    }
}