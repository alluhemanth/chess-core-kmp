package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.move.Move
import io.github.alluhemanth.chess.core.move.Offset
import io.github.alluhemanth.chess.core.piece.Piece
import io.github.alluhemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a rook.
 */
internal class RookMoveGenerator : PieceMoveGenerator {

    /**
     * Generates all pseudo-legal rook moves from the given square.
     *
     * @param piece The rook piece.
     * @param fromSquare The square the rook is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the rook.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState
    ): List<Move> {
        require(piece.pieceType == PieceType.ROOK) { "Piece must be a ROOK" }

        val rookDirections = listOf(
            Offset(0, 1),
            Offset(0, -1),
            Offset(1, 0),
            Offset(-1, 0)
        )

        return SlidingMoveGenerator.generateSlidingMoves(piece, fromSquare, board, rookDirections)
    }
}
