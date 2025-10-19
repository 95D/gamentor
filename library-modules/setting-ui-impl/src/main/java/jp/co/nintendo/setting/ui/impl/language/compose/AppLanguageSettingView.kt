package jp.co.nintendo.setting.ui.impl.language.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.nintendo.setting.ui.impl.language.viewmodel.AppLanguageSettingViewModel

@Composable
fun AppLanguageSettingView(
    modifier: Modifier = Modifier,
    appLanguageSettingViewModel: AppLanguageSettingViewModel = hiltViewModel()
) {
    val appLanguages by appLanguageSettingViewModel.appLanguagesStateFlow
        .collectAsState()

    LazyColumn(modifier = modifier) {
        items(appLanguages) { item ->
            AppLanguageOptionRow(
                viewData = item,
                onClickItem = { languageType ->
                    appLanguageSettingViewModel.selectAppLanguage(languageType)
                }
            )
        }
    }
}