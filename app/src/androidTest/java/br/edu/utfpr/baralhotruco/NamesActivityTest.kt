package br.edu.utfpr.baralhotruco

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NamesActivityTest {

    // Helper: builds and launches NamesActivity with the given player name extras.
    // Using ActivityScenario.launch(intent) instead of ActivityScenarioRule
    // because NamesActivity receives current names via Intent extras.
    private fun launch(
        p1Name: String = "Player 1:",
        p2Name: String = "Player 2:"
    ): ActivityScenario<NamesActivity> {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            NamesActivity::class.java
        ).apply {
            putExtra(NamesActivity.EXTRA_PLAYER_ONE_NAME, p1Name)
            putExtra(NamesActivity.EXTRA_PLAYER_TWO_NAME, p2Name)
        }
        return ActivityScenario.launch(intent)
    }

    // --- EditText pre-population ---

    // Verifies that the EditText fields show the names received via Intent extras.
    // NamesActivity strips the trailing colon before populating the fields,
    // so "Alice:" is displayed as "Alice".
    @Test
    fun editTexts_prePopulatedWithPassedNames() {
        launch(p1Name = "Alice:", p2Name = "Bob:")
        onView(withId(R.id.etPlayer1Name)).check(matches(withText("Alice")))
        onView(withId(R.id.etPlayer2Name)).check(matches(withText("Bob")))
    }

    // --- Bottom navigation ---

    // Verifies that the Names (Ajustes) tab is marked as selected when NamesActivity is active.
    // 1. Launches NamesActivity.
    // 2. Reads selectedItemId from the BottomNavigationView.
    // 3. Asserts it equals R.id.nav_names.
    @Test
    fun bottomNav_namesTabIsSelectedOnLaunch() {
        launch()
        onView(withId(R.id.bottomNav)).check { view, _ ->
            val nav = view as BottomNavigationView
            assertEquals(R.id.nav_names, nav.selectedItemId)
        }
    }

    // Verifies that clicking the Score tab from NamesActivity navigates to MainActivity.
    // 1. Launches NamesActivity.
    // 2. Clicks nav_score in the BottomNavigationView.
    // 3. Checks that tvPlayer1Score is visible, confirming MainActivity is in the foreground.
    @Test
    fun bottomNav_scoreTab_navigatesToMainActivity() {
        launch()
        onView(withId(R.id.nav_score)).perform(click())
        onView(withId(R.id.tvPlayer1Score)).check(matches(isDisplayed()))
    }

    // Verifies that clicking the History tab from NamesActivity navigates to HistoryActivity.
    // 1. Launches NamesActivity.
    // 2. Clicks nav_history in the BottomNavigationView.
    // 3. Checks that tvPlayer1Wins is visible, confirming HistoryActivity is in the foreground.
    @Test
    fun bottomNav_historyTab_navigatesToHistoryActivity() {
        launch()
        onView(withId(R.id.nav_history)).perform(click())
        onView(withId(R.id.tvPlayer1Wins)).check(matches(isDisplayed()))
    }

    // --- Confirm button ---

    // Verifies that clicking Confirm finishes NamesActivity with RESULT_OK and returns
    // the edited names with a colon suffix appended.
    // 1. Launches with "Alice:" and "Bob:".
    // 2. Clicks btnConfirm.
    // 3. Checks resultCode == RESULT_OK.
    // 4. Checks EXTRA_PLAYER_ONE_NAME == "Alice:" and EXTRA_PLAYER_TWO_NAME == "Bob:".
    @Test
    fun confirmButton_returnsNamesWithColonSuffix() {
        val scenario = launch(p1Name = "Alice:", p2Name = "Bob:")
        onView(withId(R.id.btnConfirm)).perform(click())
        assertEquals(Activity.RESULT_OK, scenario.result.resultCode)
        val p1 = scenario.result.resultData?.getStringExtra(NamesActivity.EXTRA_PLAYER_ONE_NAME)
        val p2 = scenario.result.resultData?.getStringExtra(NamesActivity.EXTRA_PLAYER_TWO_NAME)
        assertEquals("Alice:", p1)
        assertEquals("Bob:", p2)
    }

    // Verifies that confirming with both fields empty falls back to the default player name strings.
    // 1. Launches NamesActivity.
    // 2. Clears both EditText fields.
    // 3. Clicks Confirm.
    // 4. Checks the returned extras match the locale-appropriate default strings.
    @Test
    fun confirmButton_usesDefaultsForEmptyFields() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val scenario = launch()
        onView(withId(R.id.etPlayer1Name)).perform(clearText())
        onView(withId(R.id.etPlayer2Name)).perform(clearText())
        onView(withId(R.id.btnConfirm)).perform(click())
        assertEquals(Activity.RESULT_OK, scenario.result.resultCode)
        val p1 = scenario.result.resultData?.getStringExtra(NamesActivity.EXTRA_PLAYER_ONE_NAME)
        val p2 = scenario.result.resultData?.getStringExtra(NamesActivity.EXTRA_PLAYER_TWO_NAME)
        assertEquals(ctx.getString(R.string.player1_default), p1)
        assertEquals(ctx.getString(R.string.player2_default), p2)
    }

    // --- Reset names button ---

    // Verifies that clicking the Reset Names button replaces custom names with the defaults.
    // 1. Launches with custom names "Alice:" / "Bob:".
    // 2. Confirms the fields show "Alice" / "Bob" (stripped colon).
    // 3. Clicks btnResetNames.
    // 4. Checks neither field still shows the custom names.
    @Test
    fun resetButton_restoresDefaultNames() {
        launch(p1Name = "Alice:", p2Name = "Bob:")
        onView(withId(R.id.etPlayer1Name)).check(matches(withText("Alice")))
        onView(withId(R.id.etPlayer2Name)).check(matches(withText("Bob")))
        onView(withId(R.id.btnResetNames)).perform(click())
        onView(withId(R.id.etPlayer1Name)).check(matches(withText(not("Alice"))))
        onView(withId(R.id.etPlayer2Name)).check(matches(withText(not("Bob"))))
    }
}
