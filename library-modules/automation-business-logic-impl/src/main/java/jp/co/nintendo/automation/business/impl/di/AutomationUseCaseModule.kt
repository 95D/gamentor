package jp.co.nintendo.automation.business.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * A provider components in automation-usecase module
 */
@Module
@InstallIn(SingletonComponent::class)
object AutomationUseCaseModule {

    @Provides
    @Singleton
    @AutomationDomainCommon
    fun provideChatDataRepositoryJson(): Json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }
}
