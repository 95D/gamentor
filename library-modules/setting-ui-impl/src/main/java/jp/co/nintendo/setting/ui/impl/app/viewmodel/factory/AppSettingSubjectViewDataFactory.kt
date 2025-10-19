package jp.co.nintendo.setting.ui.impl.app.viewmodel.factory

import jakarta.inject.Inject
import jp.co.nintendo.setting.model.app.AppSettings
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailItemType
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemViewData
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingSubjectViewData
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

/**
 * A factory class for creating [AppSettingItemViewData] list
 */
class AppSettingSubjectViewDataFactory @Inject constructor() {
    fun create(appSettings: AppSettings): List<AppSettingSubjectViewData> = listOf(
        AppSettingSubjectViewData(
            name = MultiLangR.string.setting_subject_system,
            items = listOf(
                AppSettingItemViewData.DetailContent(
                    AppSettingDetailContentKey(
                        AppSettingDetailItemType.APP_LANGUAGE
                    )
                ),
                AppSettingItemViewData.DetailContent(
                    AppSettingDetailContentKey(
                        AppSettingDetailItemType.APP_THEME
                    )
                )
            )
        ),
        AppSettingSubjectViewData(
            name = MultiLangR.string.setting_subject_advance,
            items = listOf(
                AppSettingItemViewData.Switch(
                    itemKey = AppSettingItemKey.SHOW_ALL_MESSAGES,
                    isSelected = appSettings.isShownAllMessageBubbles
                ),
                AppSettingItemViewData.DetailContent(
                    AppSettingDetailContentKey(
                        AppSettingDetailItemType.CHESS_EDIT
                    )
                )
            )
        )
    )

    fun createFlattenSettingItems(
        subjects: List<AppSettingSubjectViewData>
    ): List<AppSettingItemViewData> = buildList {
        subjects.forEach { subject ->
            add(AppSettingItemViewData.SubjectTitle(name = subject.name))
            addAll(subject.items)
            add(AppSettingItemViewData.Divider)
        }
    }
}