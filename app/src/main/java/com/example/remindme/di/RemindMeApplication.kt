package com.example.remindme.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The [Application] class for the app, used for DI.
 */
@HiltAndroidApp
class RemindMeApplication : Application()