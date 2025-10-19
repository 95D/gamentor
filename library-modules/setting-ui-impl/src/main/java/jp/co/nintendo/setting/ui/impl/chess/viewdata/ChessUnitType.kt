package jp.co.nintendo.setting.ui.impl.chess.viewdata

import androidx.annotation.DrawableRes
import jp.co.nintendo.setting.ui.impl.R

/**
 * An enum class representing chess unit
 */
enum class ChessUnitType(val maximumCount: Int, @param:DrawableRes val icon: Int) {
    KING(maximumCount = 1, icon = R.drawable.chess_icon_king),
    QUEEN(maximumCount = 1, icon = R.drawable.chess_icon_queen),
    KNIGHT(maximumCount = 2, icon = R.drawable.chess_icon_knight),
    BISHOP(maximumCount = 2, icon = R.drawable.chess_icon_bishop),
    ROOK(maximumCount = 2, icon = R.drawable.chess_icon_rook),
    PAWN(maximumCount = 8, icon = R.drawable.chess_icon_pawn)
}