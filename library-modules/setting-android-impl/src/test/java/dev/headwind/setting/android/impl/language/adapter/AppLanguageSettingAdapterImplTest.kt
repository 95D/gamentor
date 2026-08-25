package dev.headwind.setting.android.impl.language.adapter

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [AppLanguageSettingAdapterImpl]
 */
@RunWith(AndroidJUnit4::class)
class AppLanguageSettingAdapterImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var target: AppLanguageSettingAdapterImpl

    @Before
    fun setUp() {
        target = AppLanguageSettingAdapterImpl()
    }

    @Test
    fun `Get current language tag`() {
        val mockLocaleList = mock<LocaleListCompat> {
            on { toLanguageTags() } doReturn "ja,en"
        }
        Mockito.mockStatic(AppCompatDelegate::class.java).use {
            it.`when`<LocaleListCompat> {
                AppCompatDelegate.getApplicationLocales()
            } doReturn mockLocaleList
            assertEquals(
                "ja",
                target.getCurrentLanguageTag()
            )
        }
    }


    @Test
    fun `Set app language tag`() {
        Mockito.mockStatic(AppCompatDelegate::class.java).use {
            target.setAppLanguageTag("ja")
            it.verify {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags("ja")
                )
            }
        }
    }

    @Test
    fun `Set app language tag with empty text`() {
        Mockito.mockStatic(AppCompatDelegate::class.java).use {
            target.setAppLanguageTag("")
            it.verify {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.getEmptyLocaleList()
                )
            }
        }
    }
}