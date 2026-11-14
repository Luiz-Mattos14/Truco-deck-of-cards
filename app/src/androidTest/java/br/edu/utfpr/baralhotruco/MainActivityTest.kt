package br.edu.utfpr.baralhotruco

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // Launches a fresh MainActivity before each test.
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // --- Score display ---

    // Verifies that both scoreboards start at "0" when the Activity first opens.
    // 1. Launches MainActivity via the rule.
    // 2. Checks that tvPlayer1Score shows "0".
    // 3. Checks that tvPlayer2Score shows "0".
    @Test
    fun scoresStartAtZero() {
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // Verifies that clicking +1 for Player 1 increments only that player's score.
    // 1. Clicks the btnJ1Tento button (+1 for Player 1).
    // 2. Checks that tvPlayer1Score now shows "1".
    // 3. Checks that tvPlayer2Score is still "0".
    @Test
    fun clickPlus1_playerOneScoreUpdates() {
        onView(withId(R.id.btnJ1Tento)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("1")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // Verifies that clicking +3 for Player 2 updates only that player's score.
    // 1. Clicks the btnJ2Truco button (+3 for Player 2).
    // 2. Checks that tvPlayer2Score now shows "3".
    @Test
    fun clickPlus3_playerTwoScoreUpdates() {
        onView(withId(R.id.btnJ2Truco)).perform(click())
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("3")))
    }

    // Verifies that consecutive clicks accumulate the score correctly (+3 then +6 = 9).
    // 1. Clicks +3 for Player 1.
    // 2. Clicks +6 for Player 1.
    // 3. Checks that tvPlayer1Score shows "9".
    @Test
    fun multipleClicks_scoresAccumulate() {
        onView(withId(R.id.btnJ1Truco)).perform(click())
        onView(withId(R.id.btnJ1Seis)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("9")))
    }

    // Verifies that the Reset Score button zeroes both scoreboards.
    // 1. Adds points to both players to produce non-zero scores.
    // 2. Clicks btnZerarPartida.
    // 3. Checks that both TextViews return to "0".
    @Test
    fun resetScoreButton_zerosBothScores() {
        onView(withId(R.id.btnJ1Seis)).perform(click())
        onView(withId(R.id.btnJ2Truco)).perform(click())
        onView(withId(R.id.btnZerarPartida)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // --- Game over ---

    // Verifies that the game over dialog appears when Player 1 reaches 12 tentos.
    // 1. Clicks +12 for Player 1 to trigger a win immediately.
    // 2. Checks that the dialog title is visible on screen.
    @Test
    fun gameOver_dialogAppearsWhenScoreReaches12() {
        onView(withId(R.id.btnJ1Doze)).perform(click())
        onView(withText(R.string.dialog_game_over_title)).check(matches(isDisplayed()))
    }

    // Verifies that "New Game" in the game over dialog resets both scoreboards.
    // 1. Clicks +12 for Player 2 to trigger game over.
    // 2. Clicks the "New Game" button in the dialog.
    // 3. Checks that both scoreboards are back to "0".
    @Test
    fun gameOver_newGameButtonResetsScores() {
        onView(withId(R.id.btnJ2Doze)).perform(click())
        onView(withText(R.string.dialog_new_game)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Score)).check(matches(withText("0")))
    }

    // --- Bottom navigation ---

    // Verifies that the Score tab is marked as selected when MainActivity first opens.
    // 1. Launches MainActivity via the rule.
    // 2. Reads selectedItemId from the BottomNavigationView.
    // 3. Asserts it equals R.id.nav_score.
    @Test
    fun bottomNav_scoreTabIsSelectedOnLaunch() {
        onView(withId(R.id.bottomNav)).check { view, _ ->
            val nav = view as BottomNavigationView
            assertEquals(R.id.nav_score, nav.selectedItemId)
        }
    }

    // Verifies that the Score tab remains selected after navigating to History and back.
    // This is the regression test for the bug where tapping nav_history would leave
    // the History tab highlighted when the user returned to MainActivity.
    // 1. Clicks nav_history — HistoryActivity opens, but Score tab must stay highlighted.
    // 2. Checks that nav_score is still the selected item (not nav_history).
    @Test
    fun bottomNav_scoreTabRemainsSelectedAfterOpeningHistory() {
        onView(withId(R.id.nav_history)).perform(click())
        onView(withId(R.id.bottomNav)).check { view, _ ->
            val nav = view as BottomNavigationView
            assertEquals(R.id.nav_score, nav.selectedItemId)
        }
    }

    // Verifies that the Score tab remains selected after tapping the Names (Ajustes) tab.
    // The listener returns false for nav_names so the visual selection stays on nav_score.
    // 1. Clicks nav_names — NamesActivity opens, but Score tab must stay highlighted.
    // 2. Checks that nav_score is still the selected item (not nav_names).
    @Test
    fun bottomNav_scoreTabRemainsSelectedAfterOpeningNames() {
        onView(withId(R.id.nav_names)).perform(click())
        onView(withId(R.id.bottomNav)).check { view, _ ->
            val nav = view as BottomNavigationView
            assertEquals(R.id.nav_score, nav.selectedItemId)
        }
    }

    // Verifies that clicking the Edit Names button opens NamesActivity.
    // 1. Clicks btnEditarNomes.
    // 2. Checks that the Player 1 EditText is visible, confirming NamesActivity is in the foreground.
    @Test
    fun editNamesButton_launchesNamesActivity() {
        onView(withId(R.id.btnEditarNomes)).perform(click())
        onView(withId(R.id.etPlayer1Name)).check(matches(isDisplayed()))
    }
}
