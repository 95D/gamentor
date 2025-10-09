package jp.co.nintendo.chat.domain.message.model.extras

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
/**
 * A sealed interface for message sender context data
 */
@Serializable
sealed interface MessageSenderExtras

/**
 * A [MessageSenderExtras] for the User sender type
 */
@Serializable
@SerialName("user")
data object AppOwnerExtras : MessageSenderExtras

/**
 * A [MessageSenderExtras] for the AI Assistant sender
 */
@Serializable
@SerialName("ai_assistant")
data class AiAssistantExtras(val responseId: String): MessageSenderExtras

/**
 * A [MessageSenderExtras] for the System sender
 */
@Serializable
@SerialName("system")
data object SystemExtras : MessageSenderExtras
