package dev.headwind.id.infra.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.headwind.id.domain.factory.EntityIdFactory
import dev.headwind.id.infra.impl.factory.EntityIdFactoryImpl
import javax.inject.Singleton

/**
 * A binding components in id-infra-impl module
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class IdInfraBindings {
    @Binds
    @Singleton
    abstract fun bindEntityIdFactory(impl: EntityIdFactoryImpl): EntityIdFactory
}
