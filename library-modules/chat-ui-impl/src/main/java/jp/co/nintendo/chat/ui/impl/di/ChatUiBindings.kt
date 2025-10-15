package jp.co.nintendo.chat.ui.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.ui.chatlist.ChatListEntry
import jp.co.nintendo.chat.ui.impl.chatlist.ChatListEntryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatUiBindings {
    @Binds
    @Singleton
    abstract fun bindChatListEntry(impl: ChatListEntryImpl): ChatListEntry
}
