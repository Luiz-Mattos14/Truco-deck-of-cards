package br.edu.utfpr.baralhotruco

// Immutable snapshot of the full game state at any point in time.
// ScoreManager produces new instances via copy() — nothing here is mutated directly.
//
// scorePlayerOne / scorePlayerTwo — current match scores (0–12).
// winsPlayerOne / winsPlayerTwo   — cumulative wins across all matches since last full reset.
// isGameOver                      — true once either player reaches WIN_SCORE.
// winner                          — 1 or 2 when isGameOver is true; null otherwise.
data class GameState(
    val scorePlayerOne: Int = 0,
    val scorePlayerTwo: Int = 0,
    val winsPlayerOne: Int = 0,
    val winsPlayerTwo: Int = 0,
    val isGameOver: Boolean = false,
    val winner: Int? = null
)
