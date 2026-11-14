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

    // Verifies that the history records events in the correct order.
    @Test
    fun addPoints_recordsHistoryInOrder() {
        var state = manager.addPoints(initial, 1, 3)
        state = manager.addPoints(state, 2, 6)
        assertEquals(2, state.history.size)
        assertEquals(ScoreEvent(1, 3, 3), state.history[0])
        assertEquals(ScoreEvent(2, 6, 6), state.history[1])
    }

    // Verifies that scoreAfter in the history reflects the value capped at 12.
    @Test
    fun addPoints_historyRecordsScoreAfterCap() {
        var state = manager.addPoints(initial, 1, 9)
        state = manager.addPoints(state, 1, 6)
        assertEquals(ScoreEvent(1, 6, 12), state.history[1])
    }

    // Verifies that reset clears scores, history, winner and game-over flag.
    @Test
    fun reset_returnsToInitialState() {
        var state = manager.addPoints(initial, 1, 9)
        state = manager.addPoints(state, 2, 6)
        val reset = manager.reset()
        assertEquals(0, reset.scorePlayerOne)
        assertEquals(0, reset.scorePlayerTwo)
        assertFalse(reset.isGameOver)
        assertNull(reset.winner)
        assertTrue(reset.history.isEmpty())
    }

    // Verifies that the initial state has no winner and zero scores.
    @Test
    fun initialState_hasNoWinner() {
        assertNull(initial.winner)
        assertFalse(initial.isGameOver)
        assertEquals(0, initial.scorePlayerOne)
        assertEquals(0, initial.scorePlayerTwo)
    }
}
