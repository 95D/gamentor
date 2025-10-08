package jp.co.nintendo.automation.domain.impl.tool.stateholder.factory

import jp.co.nintendo.automation.domain.impl.tool.stateholder.ProcessToolStateHolderImpl
import jp.co.nintendo.automation.domain.impl.tool.usecase.ProcessToolUseCaseImpl
import jp.co.nintendo.automation.domain.tool.stateholder.ProcessToolStateHolder
import jp.co.nintendo.automation.domain.tool.stateholder.factory.ProcessToolStateHolderFactory
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
