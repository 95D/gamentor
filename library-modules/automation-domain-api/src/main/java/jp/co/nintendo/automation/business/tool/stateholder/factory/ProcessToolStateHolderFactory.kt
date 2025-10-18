package jp.co.nintendo.automation.business.tool.stateholder.factory

import jp.co.nintendo.automation.business.tool.stateholder.ProcessToolStateHolder

/**
 * A factory for creating [ProcessToolStateHolder]
 */
interface ProcessToolStateHolderFactory {
    fun create(): ProcessToolStateHolder
}
