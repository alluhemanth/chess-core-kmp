package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.move.Move
import io.github.alluhemanth.chess.core.move.Offset
import io.github.alluhemanth.chess.core.piece.Piece
import io.github.alluhemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a queen.
 */
internal class QueenMoveGenerator : PieceMoveGenerator {

    /**
     * Generates all pseudo-legal queen moves from the given square.
     *
     * @param piece The queen piece.
     * @param fromSquare The square the queen is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the queen.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState
    ): List<Move> {
        require(piece.pieceType == PieceType.QUEEN) { "Piece must be a QUEEN" }

        val queenDirections = listOf(
            // Rook-like
            Offset(0, 1), Offset(0, -1), Offset(1, 0), Offset(-1, 0),
            // Bishop-like
            Offset(1, 1), Offset(1, -1), Offset(-1, 1), Offset(-1, -1)
        )

        return SlidingMoveGenerator.generateSlidingMoves(piece, fromSquare, board, queenDirections)
    }
}
