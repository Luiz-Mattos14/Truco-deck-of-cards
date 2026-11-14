package br.edu.utfpr.baralhotruco

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Verifies that both scoreboards start at "0" when the Activity opens.
    @Test
    fun scoresStartAtZero() {
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // Verifies that clicking +1 for Player 1 updates the scoreboard to "1".
    @Test
    fun clickPlus1_playerOneScoreUpdates() {
        onView(withId(R.id.btnJ1Tento)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("1")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // Verifies that clicking +3 for Player 2 updates the scoreboard to "3".
    @Test
    fun clickPlus3_playerTwoScoreUpdates() {
        onView(withId(R.id.btnJ2Truco)).perform(click())
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("3")))
    }

    // Verifies that consecutive clicks accumulate the score correctly (+3 and +6 = 9).
    @Test
    fun multipleClicks_scoresAccumulate() {
        onView(withId(R.id.btnJ1Truco)).perform(click())
        onView(withId(R.id.btnJ1Seis)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("9")))
    }

    // Verifies that the Reset Game button sets both scoreboards back to "0".
    @Test
    fun resetButton_zerosBothScores() {
        onView(withId(R.id.btnJ1Seis)).perform(click())
        onView(withId(R.id.btnJ2Truco)).perform(click())
        onView(withId(R.id.btnZerarPartida)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // Verifies that the game over dialog appears when Player 1 reaches 12 tentos.
    @Test
    fun gameOver_dialogAppearsWhenScoreReaches12() {
        onView(withId(R.id.btnJ1Doze)).perform(click())
        onView(withText(R.string.dialog_game_over_title)).check(matches(isDisplayed()))
    }

    // Verifies that clicking "New Game" in the dialog resets both scoreboards to "0".
    @Test
    fun gameOver_newGameButtonResetsScores() {
        onView(withId(R.id.btnJ2Doze)).perform(click())
        onView(withText(R.string.dialog_new_game)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }
}
