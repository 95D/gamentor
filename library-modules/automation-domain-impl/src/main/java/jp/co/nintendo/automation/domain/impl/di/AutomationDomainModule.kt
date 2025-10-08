package jp.co.nintendo.automation.domain.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * A provider components in automation-domain module
 */
@Module
@InstallIn(SingletonComponent::class)
object AutomationDomainModule {

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
