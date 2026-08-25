package dev.headwind.gamentor.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * A product [Application] class for this App
 */
@HiltAndroidApp
class GamentorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
