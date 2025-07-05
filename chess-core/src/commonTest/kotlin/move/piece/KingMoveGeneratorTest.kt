package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
import io.github.alluhemanth.chess.core.game.CastlingAvailability
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.move.assertMoveDoesNotExist
import io.github.alluhemanth.chess.core.move.assertMoveExists
import io.github.alluhemanth.chess.core.move.createDefaultGameState
import io.github.alluhemanth.chess.core.piece.Piece
import io.github.alluhemanth.chess.core.piece.PieceColor
import io.github.alluhemanth.chess.core.piece.PieceType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KingMoveGeneratorTest {

    private lateinit var kingMoveGenerator: KingMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeTest
    fun setUp() {
        kingMoveGenerator = KingMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placePiece(square: String, type: PieceType, color: PieceColor) {
        board[Square(square)] = Piece(type, color)
    }

    @Test
    fun `king moves one square in all directions on empty board`() {
        val kingSquare = Square("e4")
        placePiece("e4", PieceType.KING, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertEquals(8, moves.size)

        val expectedTargets = listOf("d5", "e5", "f5", "d4", "f4", "d3", "e3", "f3")
        expectedTargets.forEach { assertMoveExists(moves, kingSquare, Square(it)) }
    }

    @Test
    fun `king cannot move to squares with friendly pieces`() {
        val testCases = listOf(
            "d5" to "e5",
            "d4" to "f4"
        )

        testCases.forEach { (block1, block2) ->
            val kingSquare = Square("e4")
            placePiece("e4", PieceType.KING, PieceColor.WHITE)
            placePiece(block1, PieceType.PAWN, PieceColor.WHITE)
            placePiece(block2, PieceType.PAWN, PieceColor.WHITE)

            val moves = kingMoveGenerator.generatePseudoLegalMoves(
                board[kingSquare]!!, kingSquare, board, gameState
            )

            assertMoveDoesNotExist(moves, kingSquare, Square(block1))
            assertMoveDoesNotExist(moves, kingSquare, Square(block2))
        }
    }

    @Test
    fun `king can capture opponent pieces`() {
        val kingSquare = Square("e4")
        placePiece("e4", PieceType.KING, PieceColor.WHITE)
        placePiece("d5", PieceType.PAWN, PieceColor.BLACK)
        placePiece("e5", PieceType.PAWN, PieceColor.BLACK)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("d5"), isCapture = true)
        assertMoveExists(moves, kingSquare, Square("e5"), isCapture = true)
    }

    @Test
    fun `white king can castle kingside if rights and path are clear`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("g1"))
    }

    @Test
    fun `white king can castle queenside if rights and path are clear`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("a1", PieceType.ROOK, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("c1"))
    }

    @Test
    fun `king cannot castle if castling rights are revoked`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)

        gameState = gameState.copy(
            castlingRights = mapOf(
                PieceColor.WHITE to CastlingAvailability(kingside = false, queenside = false),
                PieceColor.BLACK to CastlingAvailability(kingside = true, queenside = true)
            )
        )

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveDoesNotExist(moves, kingSquare, Square("g1"))
        assertMoveDoesNotExist(moves, kingSquare, Square("c1"))
    }

    @Test
    fun `king cannot castle if path is blocked`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)
        placePiece("f1", PieceType.BISHOP, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        println(moves)
        assertMoveDoesNotExist(moves, kingSquare, Square("g1"))
    }
}