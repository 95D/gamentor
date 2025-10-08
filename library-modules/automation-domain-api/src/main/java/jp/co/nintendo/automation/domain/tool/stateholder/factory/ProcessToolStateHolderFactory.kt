package jp.co.nintendo.automation.domain.tool.stateholder.factory

import jp.co.nintendo.automation.domain.tool.stateholder.ProcessToolStateHolder
/**
 * A factory for creating [ProcessToolStateHolder]
 */
interface ProcessToolStateHolderFactory {
    fun create(): ProcessToolStateHolder
}
