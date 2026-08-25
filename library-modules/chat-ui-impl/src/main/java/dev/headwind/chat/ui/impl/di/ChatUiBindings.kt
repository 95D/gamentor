package dev.headwind.chat.ui.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.headwind.chat.ui.entry.chatlist.ChatListEntry
import dev.headwind.chat.ui.impl.chatlist.entry.ChatListEntryImpl
import javax.inject.Singleton

/**
 * A tool binding components in chat-ui-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ChatUiBindings {
    @Binds
    @Singleton
    abstract fun bindChatListEntry(impl: ChatListEntryImpl): ChatListEntry
}
