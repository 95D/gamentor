package jp.co.nintendo.automation.domain.impl.tool.actual

import android.util.Log
import jp.co.nintendo.automation.domain.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.domain.impl.tool.Tool
import jp.co.nintendo.automation.domain.tool.model.ToolParameterSignature
import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A [Tool] class for providing bot's information to AI assistant
 */
class GetBotInformationTool @Inject constructor(
    @param:AutomationDomainCommon private val json: Json
) : Tool {
    override val toolSignature: ToolSignature = ToolSignature(
        toolName = TOOL_NAME,
        toolDescription = """
                Could get bot's information.
                This information has data to be possible
                that bot checks about who the bot is and how it can help users.
            """.trimIndent(),
        parameters = ToolParameterSignature.EMPTY
    )

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

    @Serializable
    data class Result(
        val name: String,
        val role: String,
        val coreFunctions: List<String>
    )

    companion object {
        const val TOOL_NAME = "get_bot_information"
        private const val CONTENT_KEY_REASON = "reason"
    }
}
