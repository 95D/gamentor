package jp.co.nintendo.chat.data.source.local.impl.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.chat.data.source.local.impl.channel.dao.ChatChannelDao
import jp.co.nintendo.chat.data.source.local.impl.db.ChatDatabase
import jp.co.nintendo.chat.data.source.local.impl.message.dao.ChatMessageDao
import javax.inject.Singleton

/**
 * A provider components in chat-data-source-local-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
object ChatDataLocalModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase =
        Room.databaseBuilder(context, ChatDatabase::class.java, "chat_database.db")
            .build()

    @Provides
    fun provideMessageDao(database: ChatDatabase): ChatMessageDao =
        database.getChatMessageDao()

    @Provides
    fun provideChannelDao(database: ChatDatabase): ChatChannelDao =
        database.getChatChannelDao()
}
