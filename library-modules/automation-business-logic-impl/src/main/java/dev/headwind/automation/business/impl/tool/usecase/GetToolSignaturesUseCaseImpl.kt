package dev.headwind.automation.business.impl.tool.usecase

import android.util.Log
import dev.headwind.automation.model.tool.ToolSignature
import dev.headwind.automation.business.impl.di.AutomationDomainCommon
import dev.headwind.automation.business.impl.tool.ToolFactory
import dev.headwind.automation.business.tool.usecase.GetToolSignaturesUseCase
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

/**
 * An implementation of [GetToolSignaturesUseCase]
 */
class GetToolSignaturesUseCaseImpl @Inject constructor(
    private val toolFactoryMap: Map<String, @JvmSuppressWildcards ToolFactory>,
    @param:AutomationDomainCommon private val json: Json
) : GetToolSignaturesUseCase {
    init {
        Log.d("AiAssistant", "Current tools: $toolFactoryMap")
    }
    override fun getAllToolSignatures(): List<ToolSignature> =
        toolFactoryMap.values.map { it.toolSignature }

    override fun getAllToolSignaturesJson(): List<JsonElement> =
        getAllToolSignatures().mapNotNull(this::serializeOrNull)

    private fun serializeOrNull(toolSignature: ToolSignature): JsonElement? =
        try {
            json.encodeToJsonElement(toolSignature)
        } catch (_: SerializationException) {
            null
        }
}
