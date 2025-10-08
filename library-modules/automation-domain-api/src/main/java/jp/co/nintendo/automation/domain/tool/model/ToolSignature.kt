package jp.co.nintendo.automation.domain.tool.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A model class representing tool's signature
 */
@Serializable
data class ToolSignature(
    @SerialName("name")
    val toolName: String,
    @SerialName("description")
    val toolDescription: String,
    @SerialName("parameters")
    val parameters: ToolParameterSignature
)
