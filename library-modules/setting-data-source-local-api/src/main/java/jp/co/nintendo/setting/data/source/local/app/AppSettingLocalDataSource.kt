package jp.co.nintendo.setting.data.source.local.app

import jp.co.nintendo.setting.data.source.local.app.model.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * A local data source for accessing App setting values
 */
interface AppSettingLocalDataSource {
    val appSettingsEntityFlow: Flow<AppSettingsEntity>
    suspend fun update(settings: AppSettingsEntity)
}