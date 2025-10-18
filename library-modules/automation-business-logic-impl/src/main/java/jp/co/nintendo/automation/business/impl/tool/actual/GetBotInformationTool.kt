package jp.co.nintendo.automation.business.impl.tool.actual

import android.util.Log
import jp.co.nintendo.automation.model.tool.ToolParameterSignature
import jp.co.nintendo.automation.model.tool.ToolProcessLabel
import jp.co.nintendo.automation.model.tool.ToolSignature
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.business.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.business.impl.tool.ToolFactory
import jp.co.nintendo.automation.business.impl.tool.model.ToolCallState
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A [Tool] class for providing bot's information to AI assistant
 */
class GetBotInformationTool(private val json: Json) : Tool {
    override val labelBeforeStart: ToolProcessLabel
        get() = ToolProcessLabel.READ_BOT_INFORMATION

    override val labelBeforeDecide: ToolProcessLabel
        get() = ToolProcessLabel.READ_BOT_INFORMATION

    override val labelBeforeComplete: ToolProcessLabel
        get() = ToolProcessLabel.READ_BOT_INFORMATION

    private val preparedResult: Result = Result(
        name = "Gamentor bot",
        role = "Helping users use their gaming consoles and play games",
        coreFunctions = listOf(
            "Get advice on the game you're playing"
        )
    )

    override suspend fun getUserDecision(): UserDecision = UserDecision.None

    override suspend fun handle(
        userDecisionResult: UserDecisionResult,
        toolCallId: String,
        argumentsJson: String
    ): String {
        Log.d("ChunkA", "Start tool")
        return try {
            json.encodeToString(preparedResult)
        } catch (e: SerializationException) {
            json.encodeToString(mapOf(CONTENT_KEY_REASON to e::class.simpleName))
        }
    }

    override suspend fun cancel(toolCallState: ToolCallState) = Unit

    @Serializable
    data class Result(
        val name: String,
        val role: String,
        val coreFunctions: List<String>
    )

    class Factory @Inject constructor(
        @param:AutomationDomainCommon private val json: Json
    ) : ToolFactory {
        override val toolSignature: ToolSignature = ToolSignature(
            toolName = TOOL_NAME,
            toolDescription = """
                Could get bot's information.
                This information has data to be possible
                that bot checks about who the bot is and how it can help users.
            """.trimIndent(),
            parameters = ToolParameterSignature.EMPTY
        )

        override fun createTool(): GetBotInformationTool = GetBotInformationTool(json)
    }

    companion object {
        const val TOOL_NAME = "get_bot_information"
        private const val CONTENT_KEY_REASON = "reason"
    }
}
