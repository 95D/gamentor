package jp.co.nintendo.id.domain.factory

import jp.co.nintendo.id.domain.model.DomainCode

/**
 * A factory class to create entity id
 */
interface EntityIdFactory {
    fun create(domainCode: DomainCode): String
}
