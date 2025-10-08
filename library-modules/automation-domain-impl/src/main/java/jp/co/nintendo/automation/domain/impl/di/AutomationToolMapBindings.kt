package jp.co.nintendo.automation.domain.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import jp.co.nintendo.automation.domain.impl.tool.Tool
import jp.co.nintendo.automation.domain.impl.tool.actual.GetBotInformationTool

/**
 * A tool binding components in automation-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AutomationToolMapBindings {
    @Binds
    @IntoMap
    @StringKey(GetBotInformationTool.TOOL_NAME)
    abstract fun bindGetBotInformationTool(impl: GetBotInformationTool): Tool
}
