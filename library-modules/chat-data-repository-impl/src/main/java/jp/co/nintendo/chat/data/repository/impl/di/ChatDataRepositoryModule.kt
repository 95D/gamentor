package jp.co.nintendo.chat.data.repository.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * A provider components in chat-data-repository-impl moudle
 */
@Module
@InstallIn(SingletonComponent::class)
object ChatDataRepositoryModule {
    @Provides
    @Singleton
    @ChatDataRepositoryCommon
    fun provideChatDataRepositoryJson(): Json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }
}
