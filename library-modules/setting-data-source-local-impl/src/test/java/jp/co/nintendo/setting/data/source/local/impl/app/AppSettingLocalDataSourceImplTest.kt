package jp.co.nintendo.setting.data.source.local.impl.app

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.setting.data.source.local.app.AppSettingLocalDataSource
import jp.co.nintendo.setting.data.source.local.app.model.AppSettingsEntity
import jp.co.nintendo.setting.data.source.local.impl.app.serializer.AppSettingsDataStoreSerializer
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [AppSettingLocalDataSource]
 *
 * This test is including [AppSettingsDataStoreSerializer]
 */
@RunWith(AndroidJUnit4::class)
class AppSettingLocalDataSourceImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var target: AppSettingLocalDataSourceImpl

    @Before
    fun setUp() {
        target = AppSettingLocalDataSourceImpl(
            applicationContext = ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun `Update new settings`() = runTest {
        val firstSettings = AppSettingsEntity.DEFAULT
        val newSettings = firstSettings.copy(appliedThemeType = "test")
        target.appSettingsEntityFlow.test {
            assertEquals(
                firstSettings,
                awaitItem()
            )
            target.update(newSettings)
            assertEquals(
                newSettings,
                awaitItem()
            )
        }
    }
}