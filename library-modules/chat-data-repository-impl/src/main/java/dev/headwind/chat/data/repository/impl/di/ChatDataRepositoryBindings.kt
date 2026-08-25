package dev.headwind.chat.data.repository.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.headwind.chat.data.repository.channel.ChatChannelRepository
import dev.headwind.chat.data.repository.impl.channel.ChatChannelRepositoryImpl
import dev.headwind.chat.data.repository.impl.message.assistant.AiAssistantChatRepositoryImpl
import dev.headwind.chat.data.repository.message.ChatMessageRepository
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
