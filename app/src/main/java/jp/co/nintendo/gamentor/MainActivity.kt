package jp.co.nintendo.gamentor


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import jp.co.nintendo.chat.ui.chatlist.ChatListEntry
import jp.co.nintendo.design.system.theme.AppTheme
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.gamentor.theme.AppThemeDeterminant
import jp.co.nintendo.gamentor.viewmodel.AppConfigurationViewModel
import jp.co.nintendo.setting.ui.entry.AppSettingEntry
import javax.inject.Inject

/**
 * A main activity class for this app
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var chatListEntry: ChatListEntry

    @Inject
    lateinit var appSettingEntry: AppSettingEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(chatListEntry, appSettingEntry)
        }
    }
}

@Composable
private fun App(
    chatListEntry: ChatListEntry,
    appSettingEntry: AppSettingEntry,
    appConfigurationViewModel: AppConfigurationViewModel = hiltViewModel()
) {
    val userThemeType by appConfigurationViewModel.themeTypeFlow.collectAsState()
    AppTheme(
        userSemanticColors = AppThemeDeterminant.decideSemanticColors(
            isSystemInDarkTheme = isSystemInDarkTheme(),
            themeType = userThemeType
        ),
        isDarkTheme = isSystemInDarkTheme()
    ) {
        AppNavHost(chatListEntry, appSettingEntry)
    }
}

@Composable
private fun AppNavHost(
    chatListEntry: ChatListEntry,
    appSettingEntry: AppSettingEntry
) {
    val semanticColors = LocalAppSemanticColors.current
    val navController = rememberNavController()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    Box(Modifier.background(color = semanticColors.surfacePrimary)) {
        NavHost(
            navController = navController,
            startDestination = chatListEntry.route,
            modifier = Modifier.safeDrawingPadding()
        ) {
            chatListEntry.attachScreen(
                navGraphBuilder = this,
                navController = navController,
                isExpandedScreen = isExpandedScreen
            )

            appSettingEntry.attachScreen(
                navGraphBuilder = this,
                navController = navController,
                isExpandedScreen = isExpandedScreen
            )
        }
    }
}