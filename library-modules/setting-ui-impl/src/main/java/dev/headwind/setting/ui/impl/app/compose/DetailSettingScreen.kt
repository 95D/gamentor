package dev.headwind.setting.ui.impl.app.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.headwind.design.system.ui.NdsDetailScreenAppBar
import dev.headwind.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import dev.headwind.setting.ui.impl.app.viewdata.AppSettingDetailItemType
import dev.headwind.setting.ui.impl.chess.compose.ChessEditSettingView
import dev.headwind.setting.ui.impl.language.compose.AppLanguageSettingView
import dev.headwind.setting.ui.impl.theme.compose.AppThemeSettingView

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
        AppSettingDetailItemType.CHESS_EDIT -> ChessEditSettingView(modifier)
        null -> Unit
    }
}
