package jp.co.nintendo.automation.business.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import jp.co.nintendo.automation.business.impl.tool.ToolFactory
import jp.co.nintendo.automation.business.impl.tool.actual.GetBotInformationTool
import jp.co.nintendo.automation.business.impl.tool.actual.ReadCurrentChessStateTool

/**
 * A tool binding components in automation-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AutomationToolMapBindings {
    @Binds
    @IntoMap
    @StringKey(GetBotInformationTool.Companion.TOOL_NAME)
    abstract fun bindGetBotInformationTool(impl: GetBotInformationTool.Factory): ToolFactory

    @Binds
    @IntoMap
    @StringKey(ReadCurrentChessStateTool.Companion.TOOL_NAME)
    abstract fun bindReadCurrentChessStateTool(impl: ReadCurrentChessStateTool.Factory): ToolFactory
}
