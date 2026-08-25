package dev.headwind.setting.ui.impl.app.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.headwind.design.system.ui.NdsListScreenAppBar
import dev.headwind.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import dev.headwind.setting.ui.impl.app.viewdata.AppSettingItemKey
import dev.headwind.setting.ui.impl.app.viewdata.AppSettingItemViewData
import dev.headwind.setting.ui.impl.app.viewmodel.AppSettingViewModel
import dev.headwind.multi.lang.resources.R as MultiLangR

@Composable
fun AppSettingScreen(
    isExpandedScreen: Boolean,
    selectedContentKey: AppSettingDetailContentKey?,
    onNavigateToSettingItem: suspend (AppSettingDetailContentKey) -> Unit,
    onBackClicked: () -> Unit,
    appSettingViewModel: AppSettingViewModel = hiltViewModel()
) {
    val settingItems by appSettingViewModel.settingItemsStateFlow.collectAsState()
    val selectedSettingItemNameRes = selectedContentKey
        ?.itemType
        ?.settingItemName ?: MultiLangR.string.setting_app_title
    Scaffold(
        topBar = {
            NdsListScreenAppBar(
                isExpandedScreen = isExpandedScreen,
                title = stringResource(selectedSettingItemNameRes),
                onBackClicked = onBackClicked,
                isDetailSelected = selectedContentKey != null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SettingScreenContent(
                settingItems,
                selectedContentKey,
                onNavigateToSettingItem,
                appSettingViewModel::clickSettingItemKey
            )
        }
    }
}

@Composable
private fun SettingScreenContent(
    settingItems: List<AppSettingItemViewData>,
    selectedContentKey: AppSettingDetailContentKey?,
    onNavigateToSettingItem: suspend (AppSettingDetailContentKey) -> Unit,
    onClickItemKey: (AppSettingItemKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(modifier = Modifier.weight(1.0f)) {
                items(settingItems) {
                    AppSettingItemRow(
                        it,
                        selectedContentKey = selectedContentKey,
                        onNavigateToSettingItem = onNavigateToSettingItem,
                        onClickItemKey = onClickItemKey,
                    )
                }
            }
        }
    }
}