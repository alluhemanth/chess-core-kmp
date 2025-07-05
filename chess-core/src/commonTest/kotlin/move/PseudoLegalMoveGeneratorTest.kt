package io.github.alluhemanth.chess.core.move

import io.github.alluhemanth.chess.core.utils.FenUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class PseudoLegalMoveGeneratorTest {

    @Test
    fun initialMovesTest() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)

        val moves = PseudoLegalMoveGenerator.getAllPseudoLegalMoves(board, gameState)
        assertEquals(20, moves.size)
    }
}