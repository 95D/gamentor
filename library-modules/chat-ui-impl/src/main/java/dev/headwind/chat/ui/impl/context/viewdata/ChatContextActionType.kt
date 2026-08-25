package dev.headwind.chat.ui.impl.context.viewdata

import androidx.annotation.StringRes
import dev.headwind.multi.lang.resources.R

/**
 * An enum class representing context action of specific content
 */
enum class ChatContextActionType(
    @param:StringRes val actionName: Int
) {
    DELETE(R.string.message_context_action_delete)
}