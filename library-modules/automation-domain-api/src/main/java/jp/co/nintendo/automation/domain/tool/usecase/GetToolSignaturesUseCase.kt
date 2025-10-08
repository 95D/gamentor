package jp.co.nintendo.automation.domain.tool.usecase

import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import kotlinx.serialization.json.JsonElement

/**
 * An use case for providing [ToolSignature]s
 */
interface GetToolSignaturesUseCase {
    fun getAllToolSignatures(): List<ToolSignature>
    fun getAllToolSignaturesJson(): List<JsonElement>
}
