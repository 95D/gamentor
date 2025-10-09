package jp.co.nintendo.id.infra.impl.factory

import jp.co.nintendo.id.domain.factory.EntityIdFactory
import jp.co.nintendo.id.domain.model.DomainCode
import jp.co.nintendo.id.infra.impl.generator.UuidGenerator
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
