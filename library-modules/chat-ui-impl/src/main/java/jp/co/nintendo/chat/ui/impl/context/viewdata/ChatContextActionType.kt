package jp.co.nintendo.chat.ui.impl.context.viewdata

import androidx.annotation.StringRes
import jp.co.nintendo.multi.lang.resources.R

/**
 * An enum class representing context action of specific content
 */
enum class ChatContextActionType(
    @param:StringRes val actionName: Int
) {
    DELETE(R.string.message_context_action_delete)
}