package jp.co.nintendo.setting.data.source.local.impl.app.serializer

import androidx.datastore.core.Serializer
import jp.co.nintendo.setting.data.source.local.app.model.AppSettingsEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * A serializer class for serializing/deserializing for data store for [AppSettingsEntity]
 */
object AppSettingsDataStoreSerializer : Serializer<AppSettingsEntity> {
    override val defaultValue: AppSettingsEntity = AppSettingsEntity.DEFAULT

    override suspend fun readFrom(input: InputStream): AppSettingsEntity {
        return try {
            Json.decodeFromString(
                deserializer = AppSettingsEntity.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            exception.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettingsEntity, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppSettingsEntity.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}