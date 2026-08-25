package dev.headwind.automation.business.impl.tool.stateholder.factory

import dev.headwind.automation.business.tool.stateholder.ProcessToolStateHolder
import dev.headwind.automation.business.tool.stateholder.factory.ProcessToolStateHolderFactory
import dev.headwind.automation.business.impl.tool.stateholder.ProcessToolStateHolderImpl
import dev.headwind.automation.business.impl.tool.usecase.ProcessToolUseCaseImpl
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
