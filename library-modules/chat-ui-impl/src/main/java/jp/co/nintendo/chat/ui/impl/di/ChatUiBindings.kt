package jp.co.nintendo.chat.ui.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.ui.entry.chatlist.ChatListEntry
import jp.co.nintendo.chat.ui.impl.chatlist.entry.ChatListEntryImpl
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
