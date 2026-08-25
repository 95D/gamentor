package dev.headwind.chat.ui.impl.channel.viewmodel.label

import androidx.annotation.StringRes
import dev.headwind.automation.model.tool.ToolProcessLabel
import dev.headwind.automation.model.tool.decision.UserApproveLabel
import javax.inject.Inject
import dev.headwind.multi.lang.resources.R as MultiLangR

class ToolLabelProvider @Inject constructor() {

    @StringRes
    fun getUserApproveLabelString(userApproveLabel: UserApproveLabel): Int =
        when (userApproveLabel) {
            UserApproveLabel.READ_GAME_DATA ->
                MultiLangR.string.approve_request_to_read_game_data
        }

    @StringRes
    fun getToolProcessLabelString(toolProcessLabel: ToolProcessLabel): Int =
        when (toolProcessLabel) {
            ToolProcessLabel.RUNNING_TOOL -> MultiLangR.string.progress_tool_running
            ToolProcessLabel.READ_BOT_INFORMATION ->
                MultiLangR.string.progress_tool_reading_bot_information

            ToolProcessLabel.PREPARING_TOOL -> MultiLangR.string.progress_tool_preparing
            ToolProcessLabel.REQUESTING_APPROVE ->
                MultiLangR.string.progress_tool_requesting_user_approve

            ToolProcessLabel.READING_GAME_DATA -> MultiLangR.string.progress_tool_reading_game_data
        }
}