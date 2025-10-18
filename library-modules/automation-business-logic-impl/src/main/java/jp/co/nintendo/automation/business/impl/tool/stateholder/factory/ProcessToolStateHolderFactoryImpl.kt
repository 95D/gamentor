package jp.co.nintendo.automation.business.impl.tool.stateholder.factory

import jp.co.nintendo.automation.business.tool.stateholder.ProcessToolStateHolder
import jp.co.nintendo.automation.business.tool.stateholder.factory.ProcessToolStateHolderFactory
import jp.co.nintendo.automation.business.impl.tool.stateholder.ProcessToolStateHolderImpl
import jp.co.nintendo.automation.business.impl.tool.usecase.ProcessToolUseCaseImpl
import javax.inject.Inject

/**
 * An implementation [ProcessToolStateHolderFactory]
 */
class ProcessToolStateHolderFactoryImpl @Inject constructor(
    private val processToolUseCaseImpl: ProcessToolUseCaseImpl
) : ProcessToolStateHolderFactory {
    override fun create(): ProcessToolStateHolder =
        ProcessToolStateHolderImpl(processToolUseCaseImpl)
}
