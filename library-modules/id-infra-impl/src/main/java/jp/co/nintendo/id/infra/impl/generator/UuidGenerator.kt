package jp.co.nintendo.id.infra.impl.generator

import java.util.UUID
import javax.inject.Inject

class UuidGenerator @Inject constructor() {
    fun randomUUID(): UUID = UUID.randomUUID()
}
