package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.move.Move
import io.github.alluhemanth.chess.core.piece.Piece

/**
 * Interface for generating pseudo-legal moves for a chess piece.
 */
internal interface PieceMoveGenerator {
    /**
     * Generates all pseudo-legal moves for the given piece from the specified square.
     *
     * @param piece The piece to move.
     * @param fromSquare The square the piece is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the piece.
     */
    fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState
    ): List<Move>
}