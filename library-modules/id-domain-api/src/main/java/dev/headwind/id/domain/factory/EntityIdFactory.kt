package dev.headwind.id.domain.factory

import dev.headwind.id.domain.model.DomainCode

/**
 * A factory class to create entity id
 */
interface EntityIdFactory {
    fun create(domainCode: DomainCode): String
}
