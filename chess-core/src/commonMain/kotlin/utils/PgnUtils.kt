package io.github.alluhemanth.chess.core.utils

import io.github.alluhemanth.chess.core.board.Board
import io.github.alluhemanth.chess.core.game.GameState
import io.github.alluhemanth.chess.core.game.GameUtils
import io.github.alluhemanth.chess.core.game.PgnGame
import io.github.alluhemanth.chess.core.game.PgnMove

/**
 * Utility object for parsing and handling PGN (Portable Game Notation) chess games.
 * Provides methods to parse PGN strings, extract games, and apply moves to a board and game state.
 *
 * ## Overview
 * - **Parsing:** Converts PGN strings into [PgnGame] objects.
 * - **Move Application:** Applies PGN moves to a board and game state.
 *
 * ### Parsing Methods
 * - **parsePgn:** Parses a single PGN game string into a [PgnGame] object.
 * - **parsePgnGames:** Parses multiple PGN games from a text containing one or more games.
 *
 * ### Move Application
 * - **applyPgnMoves:** Applies the moves from a [PgnGame] to an initial board and game state.
 *
 * ### Example Usage
 * ```Kotlin
 * val pgnGame = PgnUtils.parsePgn(pgnString)
 * val (board, gameState) = PgnUtils.applyPgnMoves(pgnGame)
 * ```
 */
object PgnUtils {

    /**
     * Parses a single PGN game string into a [PgnGame] object.
     *
     * @param pgnString The PGN string representing a single game.
     * @return The parsed [PgnGame].
     */
    fun parsePgn(pgnString: String): PgnGame {
        val resultTokens = setOf("1-0", "0-1", "1/2-1/2", "*")

        val lines = pgnString.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val tags = mutableMapOf<String, String>()
        val moveTextBuilder = StringBuilder()
        var parsingTags = true

        for (line in lines) {
            if (parsingTags && line.startsWith("[") && line.endsWith("]")) {
                val tagName = line.substringAfter("[").substringBefore(" ")
                val tagValue = line.substringAfter("\"").substringBeforeLast("\"")
                tags[tagName] = tagValue
            } else {
                parsingTags = false
                moveTextBuilder.append(line).append(" ")
            }
        }

        val fullMoveText = moveTextBuilder.toString().trim()

        // Find result and remove it from move text
        val result = tags["Result"] ?: resultTokens.firstOrNull { fullMoveText.endsWith(it) } ?: "*"
        val moveText = fullMoveText.removeSuffix(result).trim()

        val moves = mutableListOf<PgnMove>()
        if (moveText.isEmpty()) {
            return PgnGame(tags, moves, result)
        }

        // Regex to find all tokens: move numbers, comments, or anything else (SAN moves)
        val tokenRegex = Regex("""\d+\.{1,3}|(\{[^}]*\})|(\([^)]*\))|[^\s{}()]+""")
        val tokens = tokenRegex.findAll(moveText).map { it.value }.toList()

        var lastSanMove: String? = null
        val commentsForMove = mutableListOf<String>()

        for (token in tokens) {
            when {
                token.startsWith("{") || token.startsWith("(") -> {
                    commentsForMove.add(token)
                }
                token.matches(Regex("""\d+\.{1,3}""")) -> {
                    // Ignore move numbers.
                }
                token in resultTokens -> {
                    // Ignore result tokens within the move list as they are handled separately.
                }
                else -> { // It's a SAN move
                    if (lastSanMove != null) {
                        moves.add(PgnMove(lastSanMove, commentsForMove.toList()))
                        commentsForMove.clear()
                    }
                    lastSanMove = token
                }
            }
        }

        if (lastSanMove != null) {
            moves.add(PgnMove(lastSanMove, commentsForMove.toList()))
        }

        return PgnGame(tags, moves, result)
    }

    /**
     * Parses multiple PGN games from a text containing one or more games.
     *
     * @param pgnText The PGN text containing one or more games.
     * @return A list of [PgnGame] objects.
     */
    fun parsePgnGames(pgnText: String): List<PgnGame> {
        val gameSections = pgnText
            .split(Regex("(?=\\[Event)"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return gameSections.map { parsePgn(it) }
    }

    /**
     * Applies the moves from a [PgnGame] to an initial board and game state.
     *
     * @param pgnGame The PGN game whose moves to apply.
     * @return The resulting [Board] and [GameState] after applying all moves.
     */
    fun applyPgnMoves(
        pgnGame: PgnGame
    ): Pair<Board, GameState> {
        var (currentBoard, currentGameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        val sanUtils = SanUtils

        for (pgnMove in pgnGame.moves) {
            try {
                val move = sanUtils.sanToMove(pgnMove.san, currentBoard, currentGameState)
                val (newBoard, newGameState) = GameUtils.makeMove(currentBoard, currentGameState, move)
                currentBoard = newBoard
                currentGameState = newGameState
            } catch (e: IllegalArgumentException) {
                println("Warning: Could not apply move '${pgnMove.san}': ${e.message}")
                break
            }
        }
        return Pair(currentBoard, currentGameState)
    }
}