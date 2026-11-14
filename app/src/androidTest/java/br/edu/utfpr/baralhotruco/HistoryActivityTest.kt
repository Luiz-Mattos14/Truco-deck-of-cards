package br.edu.utfpr.baralhotruco

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryActivityTest {

    // Helper: builds and launches HistoryActivity with the given win counts and player names.
    // Using ActivityScenario.launch(intent) instead of ActivityScenarioRule
    // because HistoryActivity requires Intent extras to function.
    private fun launch(
        winsP1: Int = 2,
        winsP2: Int = 1,
        p1Name: String = "Player 1:",
        p2Name: String = "Player 2:"
    ): ActivityScenario<HistoryActivity> {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            HistoryActivity::class.java
        ).apply {
            putExtra(HistoryActivity.EXTRA_WINS_PLAYER_ONE, winsP1)
            putExtra(HistoryActivity.EXTRA_WINS_PLAYER_TWO, winsP2)
            putExtra(HistoryActivity.EXTRA_PLAYER_ONE_NAME, p1Name)
            putExtra(HistoryActivity.EXTRA_PLAYER_TWO_NAME, p2Name)
        }
        return ActivityScenario.launch(intent)
    }

    // --- Bottom navigation ---

    // Verifies that the History tab is marked as selected when HistoryActivity is active.
    // 1. Launches HistoryActivity via the helper with default data.
    // 2. Finds the BottomNavigationView and reads its selectedItemId.
    // 3. Asserts it equals R.id.nav_history.
    @Test
    fun bottomNav_historyTabIsSelectedOnHistoryActivity() {
        launch()
        onView(withId(R.id.bottomNav)).check { view, _ ->
            val nav = view as BottomNavigationView
            assertEquals(R.id.nav_history, nav.selectedItemId)
        }
    }

    // Verifies that clicking the Score tab from HistoryActivity navigates back to MainActivity.
    // 1. Launches HistoryActivity.
    // 2. Clicks the nav_score item in the BottomNavigationView.
    // 3. Checks that the main screen score for Player 1 is visible, confirming MainActivity is active.
    @Test
    fun bottomNav_clickScore_navigatesBackToMainActivity() {
        launch()
        onView(withId(R.id.nav_score)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(isDisplayed()))
    }

    // --- Wins display ---

    // Verifies that Player 1's win count is displayed correctly.
    // 1. Launches HistoryActivity with winsP1 = 3.
    // 2. Checks that tvPlayer1Wins shows "3".
    @Test
    fun winsDisplay_playerOneWinsAreShown() {
        launch(winsP1 = 3, winsP2 = 0)
        onView(withId(R.id.tvPlayer1Wins)).check(matches(withText("3")))
    }

    // Verifies that Player 2's win count is displayed correctly.
    // 1. Launches HistoryActivity with winsP2 = 5.
    // 2. Checks that tvPlayer2Wins shows "5".
    @Test
    fun winsDisplay_playerTwoWinsAreShown() {
        launch(winsP1 = 0, winsP2 = 5)
        onView(withId(R.id.tvPlayer2Wins)).check(matches(withText("5")))
    }

    // Verifies that both players start with zero wins when no matches have been played.
    // 1. Launches HistoryActivity with both win counts set to 0.
    // 2. Checks that both TextViews display "0".
    @Test
    fun winsDisplay_zeroWinsShowsZero() {
        launch(winsP1 = 0, winsP2 = 0)
        onView(withId(R.id.tvPlayer1Wins)).check(matches(withText("0")))
        onView(withId(R.id.tvPlayer2Wins)).check(matches(withText("0")))
    }

    // Verifies that the player names received via Intent are shown on screen.
    // 1. Launches HistoryActivity with custom names "Alice:" and "Bob:".
    // 2. Checks that both names appear in their respective TextViews.
    @Test
    fun namesDisplay_customNamesAreShown() {
        launch(p1Name = "Alice:", p2Name = "Bob:")
        onView(withId(R.id.tvPlayer1Name)).check(matches(withText("Alice:")))
        onView(withId(R.id.tvPlayer2Name)).check(matches(withText("Bob:")))
    }

    // --- Reset button ---

    // Verifies that the Reset History button is visible on the history screen.
    // 1. Launches HistoryActivity.
    // 2. Checks that btnResetHistory is displayed.
    @Test
    fun resetButton_isVisibleOnScreen() {
        launch()
        onView(withId(R.id.btnResetHistory)).check(matches(isDisplayed()))
    }

    // Verifies that clicking the Reset History button navigates back to MainActivity.
    // 1. Launches HistoryActivity.
    // 2. Clicks btnResetHistory.
    // 3. Checks that tvPlayer1Score is visible, confirming MainActivity is in the foreground.
    @Test
    fun resetButton_click_navigatesToMainActivity() {
        launch()
        onView(withId(R.id.btnResetHistory)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(isDisplayed()))
    }
}
