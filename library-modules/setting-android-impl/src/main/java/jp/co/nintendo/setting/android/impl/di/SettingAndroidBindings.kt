package jp.co.nintendo.setting.android.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.setting.android.impl.language.adapter.AppLanguageSettingAdapterImpl
import jp.co.nintendo.setting.language.adapter.AppLanguageSettingAdapter
import javax.inject.Singleton

/**
 * A tool binding components in setting-android-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingAndroidBindings {
    @Binds
    @Singleton
    abstract fun bindAppLanguageSettingAdapter(impl: AppLanguageSettingAdapterImpl):
            AppLanguageSettingAdapter
}