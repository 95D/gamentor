package jp.co.nintendo.chat.data.source.local.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.data.source.local.channel.ChatChannelLocalDataSource
import jp.co.nintendo.chat.data.source.local.impl.channel.ChatChannelLocalDataSourceImpl
import jp.co.nintendo.chat.data.source.local.impl.message.ChatMessageLocalDataSourceImpl
import jp.co.nintendo.chat.data.source.local.message.ChatMessageLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataLocalBindings {
    @Binds
    @Singleton
    abstract fun bindChatMessageLocalDataSource(impl: ChatMessageLocalDataSourceImpl):
            ChatMessageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindChatChannelLocalDataSource(impl: ChatChannelLocalDataSourceImpl):
            ChatChannelLocalDataSource
}
