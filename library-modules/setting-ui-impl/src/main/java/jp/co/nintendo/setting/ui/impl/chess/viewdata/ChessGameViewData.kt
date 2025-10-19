package jp.co.nintendo.setting.ui.impl.chess.viewdata

/**
 * A view data representing chess game
 */
data class ChessGameViewData(
    val blackItems: List<ChessPaletteItemViewData>,
    val whiteItems: List<ChessPaletteItemViewData>,
    val gridViewData: List<List<ChessGameCellViewData>>
)