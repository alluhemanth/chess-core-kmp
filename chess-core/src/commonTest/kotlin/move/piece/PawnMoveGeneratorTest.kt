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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PawnMoveGeneratorTest {

    private lateinit var pawnMoveGenerator: PawnMoveGenerator
    private lateinit var whitePawn: Piece
    private lateinit var blackPawn: Piece
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeTest
    fun setUp() {
        pawnMoveGenerator = PawnMoveGenerator()
        whitePawn = Piece(PieceType.PAWN, PieceColor.WHITE)
        blackPawn = Piece(PieceType.PAWN, PieceColor.BLACK)
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placePiece(square: String, piece: Piece) {
        board[Square(square)] = piece
    }

    @Test
    fun `pawn forward move logic`() {

        data class PawnMoveTestCase(
            val from: String,
            val to1: String,
            val to2: String?,    // nullable
            val color: PieceColor,
            val expectedCount: Int
        )

        val testCases = listOf(
            PawnMoveTestCase("e2", "e3", "e4", PieceColor.WHITE, 2),
            PawnMoveTestCase("e3", "e4", null, PieceColor.WHITE, 1),
            PawnMoveTestCase("e7", "e6", "e5", PieceColor.BLACK, 2),
            PawnMoveTestCase("e6", "e5", null, PieceColor.BLACK, 1)
        )

        testCases.forEach { (from, to1, to2, color, expectedCount) ->
            val pawn = Piece(PieceType.PAWN, color)
            placePiece(from, pawn)

            val moves = pawnMoveGenerator.generatePseudoLegalMoves(pawn, Square(from), board, gameState)

            assertEquals(expectedCount, moves.size, "Unexpected move count for pawn at $from ($color)")
            assertMoveExists(moves, Square(from), Square(to1))
            if (!to2.isNullOrEmpty()) {
                assertMoveExists(moves, Square(from), Square(to2))
            }
        }
    }

    @Test
    fun `pawn cannot move forward when blocked`() {
        placePiece("e2", whitePawn)
        placePiece("e3", Piece(PieceType.KNIGHT, PieceColor.WHITE))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e2"), board, gameState)
        assertEquals(0, moves.size)
        assertMoveDoesNotExist(moves, Square("e2"), Square("e3"))
        assertMoveDoesNotExist(moves, Square("e2"), Square("e4"))
    }

    @Test
    fun `pawn cannot move two squares when second square is blocked`() {
        placePiece("e2", whitePawn)
        placePiece("e4", Piece(PieceType.KNIGHT, PieceColor.BLACK))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e2"), board, gameState)
        assertEquals(1, moves.size)
        assertMoveExists(moves, Square("e2"), Square("e3"))
        assertMoveDoesNotExist(moves, Square("e2"), Square("e4"))
    }

    @Test
    fun `white pawn can capture diagonally`() {
        placePiece("e4", whitePawn)
        placePiece("d5", Piece(PieceType.PAWN, PieceColor.BLACK))
        placePiece("f5", Piece(PieceType.BISHOP, PieceColor.BLACK))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e4"), board, gameState)
        assertEquals(3, moves.size)
        assertMoveExists(moves, Square("e4"), Square("e5"))
        assertMoveExists(moves, Square("e4"), Square("d5"), isCapture = true)
        assertMoveExists(moves, Square("e4"), Square("f5"), isCapture = true)
    }

    @Test
    fun `black pawn can capture diagonally`() {
        placePiece("e5", blackPawn)
        placePiece("d4", Piece(PieceType.PAWN, PieceColor.WHITE))
        placePiece("f4", Piece(PieceType.BISHOP, PieceColor.WHITE))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(blackPawn, Square("e5"), board, gameState)
        assertEquals(3, moves.size)
        assertMoveExists(moves, Square("e5"), Square("e4"))
        assertMoveExists(moves, Square("e5"), Square("d4"), isCapture = true)
        assertMoveExists(moves, Square("e5"), Square("f4"), isCapture = true)
    }

    @Test
    fun `pawn cannot capture own pieces`() {
        placePiece("e4", whitePawn)
        placePiece("d5", Piece(PieceType.KNIGHT, PieceColor.WHITE))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e4"), board, gameState)
        assertEquals(1, moves.size)
        assertMoveExists(moves, Square("e4"), Square("e5"))
        assertMoveDoesNotExist(moves, Square("e4"), Square("d5"))
    }

    @Test
    fun `white pawn promotion`() {
        placePiece("e7", whitePawn)
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e7"), board, gameState)
        val promotionSquare = Square("e8")
        val promotionMoves = moves.filter { it.to == promotionSquare }
        assertEquals(4, promotionMoves.size)
        val promotionTypes = promotionMoves.map { it.promotionPieceType }
        assertTrue(promotionTypes.contains(PieceType.QUEEN))
        assertTrue(promotionTypes.contains(PieceType.ROOK))
        assertTrue(promotionTypes.contains(PieceType.BISHOP))
        assertTrue(promotionTypes.contains(PieceType.KNIGHT))
    }

    @Test
    fun `white pawn promotion with capture`() {
        placePiece("e7", whitePawn)
        placePiece("d8", Piece(PieceType.ROOK, PieceColor.BLACK))
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e7"), board, gameState)
        val capturePromotions = moves.filter { it.to == Square("d8") && it.isCapture }
        assertEquals(4, capturePromotions.size)
        val promotionTypes = capturePromotions.map { it.promotionPieceType }
        assertTrue(promotionTypes.contains(PieceType.QUEEN))
        assertTrue(promotionTypes.contains(PieceType.ROOK))
        assertTrue(promotionTypes.contains(PieceType.BISHOP))
        assertTrue(promotionTypes.contains(PieceType.KNIGHT))
    }

    @Test
    fun `white pawn en passant capture`() {
        placePiece("e5", whitePawn)
        placePiece("f5", blackPawn)
        val enPassantSquare = Square("f6")
        val customGameState = gameState.copy(enPassantTargetSquare = enPassantSquare)
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("e5"), board, customGameState)
        assertMoveExists(moves, Square("e5"), Square("e6"))
        val enPassantMove = moves.find { it.to == enPassantSquare }
        assertNotNull(enPassantMove)
        assertTrue(enPassantMove.isCapture)
        assertTrue(enPassantMove.isEnPassantCapture)
    }

    @Test
    fun `black pawn en passant capture`() {
        placePiece("d4", blackPawn)
        placePiece("c4", whitePawn)
        val enPassantSquare = Square("c3")
        val customGameState = gameState.copy(enPassantTargetSquare = enPassantSquare)
        val moves = pawnMoveGenerator.generatePseudoLegalMoves(blackPawn, Square("d4"), board, customGameState)
        assertMoveExists(moves, Square("d4"), Square("d3"))
        val enPassantMove = moves.find { it.to == enPassantSquare }
        assertNotNull(enPassantMove)
        assertTrue(enPassantMove.isCapture)
        assertTrue(enPassantMove.isEnPassantCapture)
    }

    @Test
    fun `white pawn on a-file only moves forward and captures to the right`() {
        placePiece("a4", whitePawn)
        placePiece("b5", Piece(PieceType.BISHOP, PieceColor.BLACK))

        val moves = pawnMoveGenerator.generatePseudoLegalMoves(whitePawn, Square("a4"), board, gameState)

        assertEquals(2, moves.size)
        assertMoveExists(moves, Square("a4"), Square("a5"))

        val captures = moves.filter { it.isCapture }
        assertEquals(1, captures.size)
        assertEquals(Square("b5"), captures.single().to)
    }

    @Test
    fun `black pawn on h-file only moves down and captures to the left`() {
        placePiece("h5", blackPawn)
        placePiece("g4", Piece(PieceType.ROOK, PieceColor.WHITE))

        val moves = pawnMoveGenerator.generatePseudoLegalMoves(blackPawn, Square("h5"), board, gameState)

        assertEquals(2, moves.size)
        assertMoveExists(moves, Square("h5"), Square("h4"))

        val captures = moves.filter { it.isCapture }
        assertEquals(1, captures.size)
        assertEquals(Square("g4"), captures.single().to)
    }

}