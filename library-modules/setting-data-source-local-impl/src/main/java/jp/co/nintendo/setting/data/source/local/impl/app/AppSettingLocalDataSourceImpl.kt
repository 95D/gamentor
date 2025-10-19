package jp.co.nintendo.setting.data.source.local.impl.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import jakarta.inject.Inject
import jp.co.nintendo.setting.data.source.local.app.AppSettingLocalDataSource
import jp.co.nintendo.setting.data.source.local.app.model.AppSettingsEntity
import jp.co.nintendo.setting.data.source.local.impl.app.serializer.AppSettingsDataStoreSerializer
import kotlinx.coroutines.flow.Flow

/**
 * An implementation of [AppSettingLocalDataSource]
 */
class AppSettingLocalDataSourceImpl @Inject constructor(
    private val applicationContext: Context
) : AppSettingLocalDataSource {
    override val appSettingsEntityFlow: Flow<AppSettingsEntity> by lazy {
        applicationContext.dataStore.data
    }

    override suspend fun update(settings: AppSettingsEntity) {
        applicationContext.dataStore.updateData { settings }
    }

    private val Context.dataStore: DataStore<AppSettingsEntity> by dataStore(
        fileName = "app_settings.json",
        serializer = AppSettingsDataStoreSerializer
    )
}
