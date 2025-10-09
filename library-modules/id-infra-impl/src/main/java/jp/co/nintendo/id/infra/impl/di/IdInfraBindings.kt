package jp.co.nintendo.id.infra.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.nintendo.id.domain.factory.EntityIdFactory
import jp.co.nintendo.id.infra.impl.factory.EntityIdFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IdInfraBindings {
    @Binds
    @Singleton
    abstract fun bindEntityIdFactory(impl: EntityIdFactoryImpl): EntityIdFactory
}
