package jp.co.nintendo.setting.data.source.local.impl.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import jp.co.nintendo.setting.data.source.local.app.AppSettingLocalDataSource
import jp.co.nintendo.setting.data.source.local.impl.app.AppSettingLocalDataSourceImpl

/**
 * A binding components in setting-data-source-local-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingDataLocalModules {

    @Provides
    @Singleton
    fun provideAppSettingLocalDataSource(
        @ApplicationContext context: Context
    ): AppSettingLocalDataSource {
        return AppSettingLocalDataSourceImpl(context)
    }
}