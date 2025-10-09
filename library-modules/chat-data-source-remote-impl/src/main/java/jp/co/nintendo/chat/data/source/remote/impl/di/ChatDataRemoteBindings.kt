package jp.co.nintendo.chat.data.source.remote.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.impl.assistant.AiAssistantChatRemoteDataSourceImpl
import javax.inject.Singleton

/**
 * A binding components in chat-data-source-remote-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataRemoteBindings {
    @Binds
    @Singleton
    abstract fun bindAiAssistantChatRemoteDataSource(impl: AiAssistantChatRemoteDataSourceImpl):
            AiAssistantChatRemoteDataSource
}
