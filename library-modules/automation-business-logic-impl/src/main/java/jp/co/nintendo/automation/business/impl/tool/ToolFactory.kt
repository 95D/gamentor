package jp.co.nintendo.automation.business.impl.tool

import jp.co.nintendo.automation.model.tool.ToolSignature

/**
 * A factory class for creating tool
 */
interface ToolFactory {
    val toolSignature: ToolSignature
    fun createTool(): Tool
}