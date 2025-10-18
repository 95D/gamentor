package jp.co.nintendo.automation.business.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.automation.business.tool.stateholder.factory.ProcessToolStateHolderFactory
import jp.co.nintendo.automation.business.impl.tool.stateholder.factory.ProcessToolStateHolderFactoryImpl
import jp.co.nintendo.automation.business.impl.tool.usecase.GetToolSignaturesUseCaseImpl
import jp.co.nintendo.automation.business.tool.usecase.GetToolSignaturesUseCase
import javax.inject.Singleton

/**
 * A binding components in automation-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AutomationUseCaseBindings {
    @Binds
    @Singleton
    abstract fun bindProcessToolStateHolderFactory(
        impl: ProcessToolStateHolderFactoryImpl
    ): ProcessToolStateHolderFactory

    @Binds
    @Singleton
    abstract fun bindGetToolSignaturesUseCase(
        impl: GetToolSignaturesUseCaseImpl
    ): GetToolSignaturesUseCase
}
