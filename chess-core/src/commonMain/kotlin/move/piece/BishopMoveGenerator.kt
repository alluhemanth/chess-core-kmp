package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.move.Move
import io.github.alluhemanth.chess.core.move.Offset
import io.github.alluhemanth.chess.core.piece.Piece
import io.github.alluhemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a bishop.
 */
internal class BishopMoveGenerator : PieceMoveGenerator {

    /**
     * Generates all pseudo-legal bishop moves from the given square.
     *
     * @param piece The bishop piece.
     * @param fromSquare The square the bishop is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the bishop.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState
    ): List<Move> {
        require(piece.pieceType == PieceType.BISHOP) { "Piece must be a BISHOP" }

        val bishopDirections = listOf(
            Offset(1, 1), Offset(1, -1),
            Offset(-1, 1), Offset(-1, -1)
        )

        return SlidingMoveGenerator.generateSlidingMoves(piece, fromSquare, board, bishopDirections)
    }
}
