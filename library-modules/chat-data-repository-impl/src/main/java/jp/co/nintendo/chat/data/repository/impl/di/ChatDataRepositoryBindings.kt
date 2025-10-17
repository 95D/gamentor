package jp.co.nintendo.chat.data.repository.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.data.repository.channel.ChatChannelRepository
import jp.co.nintendo.chat.data.repository.impl.channel.ChatChannelRepositoryImpl
import jp.co.nintendo.chat.data.repository.impl.message.assistant.AiAssistantChatRepositoryImpl
import jp.co.nintendo.chat.data.repository.message.ChatMessageRepository
import javax.inject.Singleton


/**
 * A tool binding components in chat-data-repository-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataRepositoryBindings {
    @Binds
    @Singleton
    abstract fun bindAiAssistantChatRepository(
        impl: AiAssistantChatRepositoryImpl
    ): ChatMessageRepository

    @Binds
    @Singleton
    abstract fun bindChatChannelRepository(
        impl: ChatChannelRepositoryImpl
    ): ChatChannelRepository
}
