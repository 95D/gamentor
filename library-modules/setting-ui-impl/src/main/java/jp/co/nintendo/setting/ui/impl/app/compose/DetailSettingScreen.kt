package jp.co.nintendo.setting.ui.impl.app.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jp.co.nintendo.design.system.ui.NdsDetailScreenAppBar
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailItemType
import jp.co.nintendo.setting.ui.impl.language.compose.AppLanguageSettingView
import jp.co.nintendo.setting.ui.impl.theme.compose.AppThemeSettingView

@Composable
fun DetailSettingScreen(
    isExpandedScreen: Boolean,
    selectedItem: AppSettingDetailContentKey?,
    onBackClicked: () -> Unit
) {
    val settingItemNameRes = selectedItem
        ?.itemType
        ?.settingItemName

    Scaffold(
        topBar = {
            NdsDetailScreenAppBar(
                isExpandedScreen = isExpandedScreen,
                title = settingItemNameRes?.let { stringResource(it) }.orEmpty(),
                onBackClicked = onBackClicked
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DetailSettingScreenContent(selectedItem)
        }
    }
}

@Composable
private fun DetailSettingScreenContent(
    selectedItem: AppSettingDetailContentKey?,
    modifier: Modifier = Modifier
) {
    when (selectedItem?.itemType) {
        AppSettingDetailItemType.APP_LANGUAGE -> AppLanguageSettingView(modifier)
        AppSettingDetailItemType.APP_THEME -> AppThemeSettingView(modifier)
        null -> Unit
    }
}
