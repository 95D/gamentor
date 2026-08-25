package dev.headwind.automation.business.tool.stateholder.factory

import dev.headwind.automation.business.tool.stateholder.ProcessToolStateHolder

/**
 * A factory for creating [ProcessToolStateHolder]
 */
interface ProcessToolStateHolderFactory {
    fun create(): ProcessToolStateHolder
}
