package br.edu.utfpr.baralhotruco

// Handles all scoring logic for a Truco match.
// Keeps no mutable state — every method takes a GameState and returns a new one.
class ScoreManager {

    companion object {
        // A player wins the match when their score reaches this value.
        const val WIN_SCORE = 12
    }

    // Adds points to the given player and returns an updated GameState.
    // 1. Returns the state unchanged if the game is already over.
    // 2. Reads the player's current score and adds the requested points.
    // 3. Caps the resulting score at WIN_SCORE so it never exceeds 12.
    // 4. Checks whether either player has now reached WIN_SCORE.
    // 5. If there is a winner, increments that player's win counter.
    // 6. Returns a copy of the state with the updated score, win count,
    //    and game-over / winner fields.
    fun addPoints(state: GameState, player: Int, points: Int): GameState {
        if (state.isGameOver) return state

        val current = if (player == 1) state.scorePlayerOne else state.scorePlayerTwo
        val newScore = minOf(current + points, WIN_SCORE)
        val newP1 = if (player == 1) newScore else state.scorePlayerOne
        val newP2 = if (player == 2) newScore else state.scorePlayerTwo

        val winner = when {
            newP1 >= WIN_SCORE -> 1
            newP2 >= WIN_SCORE -> 2
            else -> null
        }

        return state.copy(
            scorePlayerOne = newP1,
            scorePlayerTwo = newP2,
            winsPlayerOne = if (winner == 1) state.winsPlayerOne + 1 else state.winsPlayerOne,
            winsPlayerTwo = if (winner == 2) state.winsPlayerTwo + 1 else state.winsPlayerTwo,
            isGameOver = winner != null,
            winner = winner
        )
    }

    // Resets scores and game-over flag for a new match while preserving win counters.
    // Called after the game-over dialog is dismissed so accumulated wins are not lost.
    fun resetMatch(state: GameState): GameState = state.copy(
        scorePlayerOne = 0,
        scorePlayerTwo = 0,
        isGameOver = false,
        winner = null
    )

    // Zeroes win counters while keeping the current match scores and game-over state.
    // Called when the user taps "Reset History" on the history screen.
    fun resetWins(state: GameState): GameState = state.copy(
        winsPlayerOne = 0,
        winsPlayerTwo = 0
    )

    // Returns a completely fresh GameState with zeroed scores and win counters.
    // Called by the "Zerar Histórico" button to start tracking from scratch.
    fun resetAll(): GameState = GameState()
}
