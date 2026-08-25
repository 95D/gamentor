package dev.headwind.setting.ui.impl.app.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A content key for navigating detail setting page of [AppSettingDetailItemType]
 */
@Parcelize
data class AppSettingDetailContentKey(val itemType: AppSettingDetailItemType): Parcelable