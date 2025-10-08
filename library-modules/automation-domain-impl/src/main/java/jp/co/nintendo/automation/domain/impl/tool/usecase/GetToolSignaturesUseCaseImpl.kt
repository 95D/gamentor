package jp.co.nintendo.automation.domain.impl.tool.usecase

import android.util.Log
import jp.co.nintendo.automation.domain.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.domain.impl.tool.Tool
import jp.co.nintendo.automation.domain.tool.usecase.GetToolSignaturesUseCase
import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

/**
 * An implementation of [GetToolSignaturesUseCase]
 */
class GetToolSignaturesUseCaseImpl @Inject constructor(
    private val toolMap: Map<String, @JvmSuppressWildcards Tool>,
    @param:AutomationDomainCommon private val json: Json
) : GetToolSignaturesUseCase {
    init {
        Log.d("AiAssistant", "Current tools: $toolMap")
    }
    override fun getAllToolSignatures(): List<ToolSignature> =
        toolMap.values.map { it.toolSignature }

    override fun getAllToolSignaturesJson(): List<JsonElement> =
        getAllToolSignatures().mapNotNull(this::serializeOrNull)

    private fun serializeOrNull(toolSignature: ToolSignature): JsonElement? =
        try {
            json.encodeToJsonElement(toolSignature)
        } catch (_: SerializationException) {
            null
        }
}
