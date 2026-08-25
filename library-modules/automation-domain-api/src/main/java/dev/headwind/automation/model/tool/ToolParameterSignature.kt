package dev.headwind.automation.model.tool

import kotlinx.serialization.Serializable

/**
 * A model class representing Tool's parameter signature
 */
@Serializable
data class ToolParameterSignature(
    val type: String,
    val description: String? = null,
    val properties: Map<String, ToolParameterSignature>? = null,
    val items: ToolParameterSignature? = null,
    val required: List<String>? = null,
    val enum: List<String>? = null
) {
    companion object {
        const val TYPE_OBJECT = "object"
        val EMPTY: ToolParameterSignature = ToolParameterSignature(
            type = TYPE_OBJECT,
            properties = emptyMap()
        )
    }
}
