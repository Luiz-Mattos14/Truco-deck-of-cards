package br.edu.utfpr.baralhotruco

class ScoreManager {

    companion object {
        const val WIN_SCORE = 12
    }

    // Adds points to the given player and returns the new game state.
    // If the game is already over, the state is returned unchanged.
    // The score is capped at WIN_SCORE (12 tentos).
    fun addPoints(state: GameState, player: Int, points: Int): GameState {
        if (state.isGameOver) return state

        val currentScore = if (player == 1) state.scorePlayerOne else state.scorePlayerTwo
        val newScore = minOf(currentScore + points, WIN_SCORE)
        val event = ScoreEvent(player, points, newScore)

        val newScoreP1 = if (player == 1) newScore else state.scorePlayerOne
        val newScoreP2 = if (player == 2) newScore else state.scorePlayerTwo

        val winner = when {
            newScoreP1 >= WIN_SCORE -> 1
            newScoreP2 >= WIN_SCORE -> 2
            else -> null
        }

        return state.copy(
            scorePlayerOne = newScoreP1,
            scorePlayerTwo = newScoreP2,
            history = state.history + event,
            isGameOver = winner != null,
            winner = winner
        )
    }

    // Returns a fresh GameState with zeroed scores and empty history.
    fun reset(): GameState = GameState()
}
