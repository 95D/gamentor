package jp.co.nintendo.setting.ui.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.setting.ui.entry.AppSettingEntry
import jp.co.nintendo.setting.ui.impl.app.entry.AppSettingEntryImpl
import javax.inject.Singleton

/**
 * A binding components in setting-ui-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingUiBindings {
    @Binds
    @Singleton
    abstract fun bindAppSettingEntry(impl: AppSettingEntryImpl): AppSettingEntry
}