package dev.headwind.id.infra.impl.factory

import dev.headwind.id.domain.factory.EntityIdFactory
import dev.headwind.id.domain.model.DomainCode
import dev.headwind.id.infra.impl.generator.UuidGenerator
import javax.inject.Inject

/**
 * An implementation of [EntityIdFactory]
 */
class EntityIdFactoryImpl @Inject constructor(
    private val uuidGenerator: UuidGenerator
) : EntityIdFactory {
    override fun create(domainCode: DomainCode): String {
        val code = domainCode.code
        val randomId = uuidGenerator.randomUUID().toString()
        return "${code}_$randomId"
    }
}
