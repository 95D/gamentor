package jp.co.nintendo.gamentor


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import jp.co.nintendo.chat.ui.chatlist.ChatListEntry
import jp.co.nintendo.design.system.theme.AppTheme
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import javax.inject.Inject

/**
 * A main activity class for this app
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var chatListEntry: ChatListEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(isDarkTheme = isSystemInDarkTheme()) {
                AppNavHost(chatListEntry)
            }
        }
    }
}

@Composable
fun AppNavHost(
    chatListEntry: ChatListEntry
) {
    val semanticColors = LocalAppSemanticColors.current
    val navController = rememberNavController()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )

    NavHost(
        navController = navController,
        startDestination = chatListEntry.route,
        modifier = Modifier
            .background(color = semanticColors.surfacePrimary)
    ) {
        chatListEntry.attachScreen(
            navGraphBuilder = this,
            navController = navController,
            isExpandedScreen = isExpandedScreen
        )
    }
}

