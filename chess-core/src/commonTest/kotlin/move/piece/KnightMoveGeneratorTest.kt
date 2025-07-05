package io.github.alluhemanth.chess.core.move.piece

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.board.Square
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

class KnightMoveGeneratorTest {

    private lateinit var knightMoveGenerator: KnightMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeTest
    fun setUp() {
        knightMoveGenerator = KnightMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placeKnight(square: String, color: PieceColor = PieceColor.WHITE) {
        board[Square(square)] = Piece(PieceType.KNIGHT, color)
    }

    private fun placePawn(square: String, color: PieceColor) {
        board[Square(square)] = Piece(PieceType.PAWN, color)
    }

    @Test
    fun `knight moves in L-shape on an empty board`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        val expectedTargets = setOf("b3", "b5", "c2", "c6", "e2", "e6", "f3", "f5").map { Square(it) }.toSet()
        assertEquals(8, moves.size)
        assertEquals(expectedTargets, moves.map { it.to }.toSet())
    }

    @Test
    fun `knight cannot move to squares occupied by friendly pieces`() {
        val testCases = listOf(
            Triple("d4", "b3", "f5"),
            Triple("d4", "c2", "e6")
        )

        testCases.forEach { (knight, block1, block2) ->
            placeKnight(knight)
            placePawn(block1, PieceColor.WHITE)
            placePawn(block2, PieceColor.WHITE)

            val moves = knightMoveGenerator.generatePseudoLegalMoves(
                board[Square(knight)]!!, Square(knight), board, gameState
            )

            assertMoveDoesNotExist(moves, Square(knight), Square(block1))
            assertMoveDoesNotExist(moves, Square(knight), Square(block2))
        }
    }

    @Test
    fun `knight can capture opponent pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        placePawn("b3", PieceColor.BLACK)
        placePawn("f5", PieceColor.BLACK)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertMoveExists(moves, knightSquare, Square("b3"), isCapture = true)
        assertMoveExists(moves, knightSquare, Square("f5"), isCapture = true)
    }

    @Test
    fun `knight on edge or corner has limited moves`() {

        data class KnightEdgeTestCase(
            val knight: String,
            val expectedCount: Int,
            val move1: String,
            val move2: String
        )

        val testCases = listOf(
            KnightEdgeTestCase("a1", 2, "b3", "c2"),
            KnightEdgeTestCase("a4", 4, "b2", "b6"),
            KnightEdgeTestCase("a4", 4, "c3", "c5"),
            KnightEdgeTestCase("h8", 2, "g6", "f7")
        )

        testCases.forEach { (knight, expectedCount, move1, move2) ->
            placeKnight(knight)

            val moves = knightMoveGenerator.generatePseudoLegalMoves(
                board[Square(knight)]!!, Square(knight), board, gameState
            )

            assertEquals(expectedCount, moves.size, "Unexpected move count for knight at $knight")
            assertMoveExists(moves, Square(knight), Square(move1))
            assertMoveExists(moves, Square(knight), Square(move2))
        }
    }

    @Test
    fun `knight can jump over other pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        // Place blocking pieces
        placePawn("d3", PieceColor.WHITE)
        placePawn("d5", PieceColor.BLACK)
        placePawn("c4", PieceColor.WHITE)
        placePawn("e4", PieceColor.BLACK)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertEquals(8, moves.size)
    }

    @Test
    fun `black knight can capture white pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4", PieceColor.BLACK)
        placePawn("b3", PieceColor.WHITE)
        placePawn("f5", PieceColor.WHITE)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertMoveExists(moves, knightSquare, Square("b3"), isCapture = true)
        assertMoveExists(moves, knightSquare, Square("f5"), isCapture = true)
    }
}