package br.edu.utfpr.baralhotruco

import org.junit.Assert.*
import org.junit.Test

class ScoreManagerTest {

    private val manager = ScoreManager()
    private val initial = GameState()

    // Verifies that addPoints correctly adds 1 tento to Player 1.
    @Test
    fun addPoints_addsToPlayerOne() {
        val state = manager.addPoints(initial, 1, 1)
        assertEquals(1, state.scorePlayerOne)
        assertEquals(0, state.scorePlayerTwo)
    }

    // Verifies that addPoints correctly adds 3 tentos to Player 2.
    @Test
    fun addPoints_addsToPlayerTwo() {
        val state = manager.addPoints(initial, 2, 3)
        assertEquals(0, state.scorePlayerOne)
        assertEquals(3, state.scorePlayerTwo)
    }

    // Verifies that consecutive calls accumulate the score correctly.
    @Test
    fun addPoints_accumulatesCorrectly() {
        var state = manager.addPoints(initial, 1, 3)
        state = manager.addPoints(state, 1, 6)
        assertEquals(9, state.scorePlayerOne)
    }

    // Verifies that the score does not exceed 12 even when the sum would surpass it.
    @Test
    fun addPoints_capsScoreAt12() {
        var state = manager.addPoints(initial, 1, 9)
        state = manager.addPoints(state, 1, 6)
        assertEquals(12, state.scorePlayerOne)
    }

    // Verifies that the game ends and Player 1 is declared winner upon reaching 12.
    @Test
    fun addPoints_gameOverWhenPlayerOneReaches12() {
        val state = manager.addPoints(initial, 1, 12)
        assertTrue(state.isGameOver)
        assertEquals(1, state.winner)
    }

    // Verifies that the game ends and Player 2 is declared winner upon reaching 12.
    @Test
    fun addPoints_gameOverWhenPlayerTwoReaches12() {
        var state = manager.addPoints(initial, 2, 9)
        state = manager.addPoints(state, 2, 3)
        assertTrue(state.isGameOver)
        assertEquals(2, state.winner)
    }

    // Verifies that no points are added after the game is over.
    @Test
    fun addPoints_doesNothingAfterGameOver() {
        var state = manager.addPoints(initial, 1, 12)
        state = manager.addPoints(state, 2, 12)
        assertEquals(0, state.scorePlayerTwo)
    }

    // Verifies that Player 1's win counter is incremented when they win a match.
    @Test
    fun addPoints_incrementsWinsForPlayerOne() {
        val state = manager.addPoints(initial, 1, 12)
        assertEquals(1, state.winsPlayerOne)
        assertEquals(0, state.winsPlayerTwo)
    }

    // Verifies that Player 2's win counter is incremented when they win a match.
    @Test
    fun addPoints_incrementsWinsForPlayerTwo() {
        var state = manager.addPoints(initial, 2, 9)
        state = manager.addPoints(state, 2, 3)
        assertEquals(0, state.winsPlayerOne)
        assertEquals(1, state.winsPlayerTwo)
    }

    // Verifies that win counters accumulate correctly across multiple matches.
    @Test
    fun resetMatch_preservesWinCounters() {
        var state = manager.addPoints(initial, 1, 12)
        state = manager.resetMatch(state)
        state = manager.addPoints(state, 2, 12)
        state = manager.resetMatch(state)
        assertEquals(1, state.winsPlayerOne)
        assertEquals(1, state.winsPlayerTwo)
    }

    // Verifies that resetMatch clears scores and game-over but keeps win counts.
    @Test
    fun resetMatch_clearsScoresAndKeepsWins() {
        var state = manager.addPoints(initial, 1, 12)
        state = manager.resetMatch(state)
        assertEquals(0, state.scorePlayerOne)
        assertEquals(0, state.scorePlayerTwo)
        assertFalse(state.isGameOver)
        assertNull(state.winner)
        assertEquals(1, state.winsPlayerOne)
    }

    // Verifies that resetWins zeroes win counters while keeping current scores untouched.
    // 1. Gives Player 1 a mid-game score of 6.
    // 2. Wins a match for Player 2 to build a non-zero win count.
    // 3. Calls resetWins() and checks wins are zero while the mid-game score survives.
    @Test
    fun resetWins_zeroesWinsButKeepsScores() {
        var state = manager.addPoints(initial, 1, 6)
        state = manager.addPoints(state, 2, 12)
        state = manager.resetMatch(state)
        state = manager.addPoints(state, 1, 6)
        state = manager.resetWins(state)
        assertEquals(0, state.winsPlayerOne)
        assertEquals(0, state.winsPlayerTwo)
        assertEquals(6, state.scorePlayerOne)
    }

    // Verifies that resetAll returns a completely fresh state with zeroed scores and wins.
    @Test
    fun resetAll_returnsToInitialState() {
        var state = manager.addPoints(initial, 1, 12)
        state = manager.resetMatch(state)
        state = manager.addPoints(state, 2, 12)
        val reset = manager.resetAll()
        assertEquals(0, reset.scorePlayerOne)
        assertEquals(0, reset.scorePlayerTwo)
        assertEquals(0, reset.winsPlayerOne)
        assertEquals(0, reset.winsPlayerTwo)
        assertFalse(reset.isGameOver)
        assertNull(reset.winner)
    }

    // Verifies that the initial state has no winner and zero scores and wins.
    @Test
    fun initialState_hasNoWinner() {
        assertNull(initial.winner)
        assertFalse(initial.isGameOver)
        assertEquals(0, initial.scorePlayerOne)
        assertEquals(0, initial.scorePlayerTwo)
        assertEquals(0, initial.winsPlayerOne)
        assertEquals(0, initial.winsPlayerTwo)
    }
}
