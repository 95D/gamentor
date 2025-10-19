package jp.co.nintendo.setting.data.repository.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.data.repository.impl.app.AppSettingRepositoryImpl
import javax.inject.Singleton

/**
 * A tool binding components in setting-data-repository-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingDataRepositoryBindings {
    @Binds
    @Singleton
    abstract fun bindAppSettingDataRepository(impl: AppSettingRepositoryImpl): AppSettingRepository
}