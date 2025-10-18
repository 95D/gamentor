package jp.co.nintendo.automation.business.tool.usecase

import jp.co.nintendo.automation.model.tool.ToolSignature
import kotlinx.serialization.json.JsonElement

/**
 * An use case for providing [jp.co.nintendo.automation.model.tool.ToolSignature]s
 */
interface GetToolSignaturesUseCase {
    fun getAllToolSignatures(): List<ToolSignature>
    fun getAllToolSignaturesJson(): List<JsonElement>
}