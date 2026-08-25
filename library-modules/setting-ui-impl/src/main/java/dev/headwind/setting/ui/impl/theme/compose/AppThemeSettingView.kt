package dev.headwind.setting.ui.impl.theme.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.headwind.setting.ui.impl.theme.viewmodel.AppThemeSettingViewModel


@Composable
fun AppThemeSettingView(
    modifier: Modifier = Modifier,
    appThemeSettingViewModel: AppThemeSettingViewModel = hiltViewModel()
) {
    val appThemes by appThemeSettingViewModel.appThemesStateFlow
        .collectAsState()

    LazyColumn(modifier = modifier) {
        items(appThemes) { item ->
            AppThemeSettingOptionRow(
                viewData = item,
                onClickItem = { themeViewType ->
                    appThemeSettingViewModel.selectTheme(themeViewType)
                }
            )
        }
    }
}