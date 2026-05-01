package io.github.alluhemanth.chess.core.game

data class PgnMove(
    val san: String,
    val comments: List<String> = emptyList()
)
