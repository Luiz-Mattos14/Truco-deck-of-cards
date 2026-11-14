package br.edu.utfpr.baralhotruco

data class ScoreEvent(
    val player: Int,
    val points: Int,
    val scoreAfter: Int
)

data class GameState(
    val scorePlayerOne: Int = 0,
    val scorePlayerTwo: Int = 0,
    val history: List<ScoreEvent> = emptyList(),
    val isGameOver: Boolean = false,
    val winner: Int? = null
)
