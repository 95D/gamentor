package jp.co.nintendo.chat.data.source.remote.assistant.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * A data transfer object representing catalog of tool which is invoked by AI
 */
@Serializable
data class ToolCatalogDto(
    val type: String,
    val function: JsonElement
)
