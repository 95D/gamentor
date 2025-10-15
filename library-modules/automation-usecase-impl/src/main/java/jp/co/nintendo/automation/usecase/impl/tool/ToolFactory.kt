package jp.co.nintendo.automation.usecase.impl.tool

import jp.co.nintendo.automation.domain.tool.model.ToolSignature

/**
 * A factory class for creating tool
 */
interface ToolFactory {
    val toolSignature: ToolSignature
    fun createTool(): Tool
}