package jp.co.nintendo.chat.data.source.remote.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.data.source.remote.impl.assistant.stream.ChunkAssembleTask
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * A provider components in chat-data-source-remote-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
object ChatDataRemoteModule {
    @Provides
    @Singleton
    @ChatDataRemoteCommon
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @ChatDataRemoteCommon
    fun provideChatBaseUrl(): HttpUrl = "https://chatproxystream-vg267afxqq-du.a.run.app/"
        .toHttpUrl()

    @Provides
    @Singleton
    @ChatDataRemoteCommon
    fun provideChatDataRemoteJson(): Json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    @ChatDataRemoteCommon
    fun chunkAssembleTaskSupplier(
        @ChatDataRemoteCommon json: Json
    ): () -> ChunkAssembleTask = { ChunkAssembleTask(json) }
}
