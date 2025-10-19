package jp.co.nintendo.setting.data.repository.impl.app

import jakarta.inject.Inject
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.data.repository.impl.app.chess.mapper.ChessUnitMapper
import jp.co.nintendo.setting.data.source.local.app.AppSettingLocalDataSource
import jp.co.nintendo.setting.model.app.AppSettings
import jp.co.nintendo.setting.model.app.chess.ChessUnit
import jp.co.nintendo.setting.model.app.theme.AppThemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An implementation of [AppSettingRepository]
 */
class AppSettingRepositoryImpl @Inject constructor(
    private val localDataSource: AppSettingLocalDataSource,
    private val chessUnitMapper: ChessUnitMapper
) : AppSettingRepository {
    override val appSettingsFlow: Flow<AppSettings> by lazy {
        localDataSource.appSettingsEntityFlow.map {
            AppSettings(
                appliedThemeType = getAppThemeType(it.appliedThemeType),
                isShownAllMessageBubbles = it.isShownAllMessageBubbles,
                simulatedChessUnits = it.simulatedChessUnits.map(chessUnitMapper::mapToDomain)
            )
        }
    }

    private fun getAppThemeType(name: String): AppThemeType =
        AppThemeType.entries.firstOrNull { it.name == name } ?: AppThemeType.DEVICE

    override suspend fun updateApplyThemeType(themeType: AppThemeType) =
        withContext(Dispatchers.IO) {
            val nextSettings = localDataSource.appSettingsEntityFlow
                .first()
                .copy(appliedThemeType = themeType.name)
            localDataSource.update(nextSettings)
        }

    override suspend fun updateIsShownAllMessageBubbles(isShown: Boolean) =
        withContext(Dispatchers.IO) {
            val nextSettings = localDataSource.appSettingsEntityFlow
                .first()
                .copy(isShownAllMessageBubbles = isShown)
            localDataSource.update(nextSettings)
        }

    override suspend fun updateChessSimulation(chessUnits: List<ChessUnit>) {
        withContext(Dispatchers.IO) {
            val nextSettings = localDataSource.appSettingsEntityFlow
                .first()
                .copy(simulatedChessUnits = chessUnits.map(chessUnitMapper::mapToEntity))
            localDataSource.update(nextSettings)
        }
    }
}