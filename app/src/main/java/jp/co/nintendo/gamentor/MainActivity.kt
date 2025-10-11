package jp.co.nintendo.gamentor


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import jp.co.nintendo.design.system.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import jp.co.nintendo.chat.ui.channel.ChatChannelEntry
import jp.co.nintendo.chat.ui.chatlist.ChatChannelListEntry
import javax.inject.Inject

/**
 * A main activity class for this app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var chatChannelEntry: ChatChannelEntry

    @Inject
    lateinit var chatChannelListEntry: ChatChannelListEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(isDarkTheme = isSystemInDarkTheme()) {
                AppNavHost(chatChannelListEntry, chatChannelEntry) }
            }
    }
}

@Composable
fun AppNavHost(
    chatChannelListEntry: ChatChannelListEntry,
    chatChannelEntry: ChatChannelEntry
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = chatChannelListEntry.route
    ) {
        chatChannelListEntry.attachScreen(this, navController)
        chatChannelEntry.attachScreen(this, navController)
    }
}

