package dev.headwind.automation.business.tool.usecase

import dev.headwind.automation.model.tool.ToolSignature
import kotlinx.serialization.json.JsonElement

/**
 * An use case for providing [dev.headwind.automation.model.tool.ToolSignature]s
 */
interface GetToolSignaturesUseCase {
    fun getAllToolSignatures(): List<ToolSignature>
    fun getAllToolSignaturesJson(): List<JsonElement>
}