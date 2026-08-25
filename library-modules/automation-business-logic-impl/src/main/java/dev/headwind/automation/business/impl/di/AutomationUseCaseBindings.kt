package dev.headwind.automation.business.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.headwind.automation.business.tool.stateholder.factory.ProcessToolStateHolderFactory
import dev.headwind.automation.business.impl.tool.stateholder.factory.ProcessToolStateHolderFactoryImpl
import dev.headwind.automation.business.impl.tool.usecase.GetToolSignaturesUseCaseImpl
import dev.headwind.automation.business.tool.usecase.GetToolSignaturesUseCase
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
