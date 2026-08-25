package dev.headwind.automation.business.impl.tool

import dev.headwind.automation.model.tool.ToolSignature

/**
 * A factory class for creating tool
 */
interface ToolFactory {
    val toolSignature: ToolSignature
    fun createTool(): Tool
}