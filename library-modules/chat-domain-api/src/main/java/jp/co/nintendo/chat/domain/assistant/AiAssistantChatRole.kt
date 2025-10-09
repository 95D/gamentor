package jp.co.nintendo.chat.domain.assistant

/**
 * An enum class representing the role in AI assistant chat
 */
enum class AiAssistantChatRole(val roleName: String) {
    USER(roleName = "user"),
    AI_ASSISTANT(roleName = "assistant"),
    SYSTEM(roleName = "system"),
    TOOL(roleName = "tool"),
    UNKNOWN(roleName = "unknown")
}
