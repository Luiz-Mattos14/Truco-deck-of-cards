package br.edu.utfpr.baralhotruco

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val scoreManager = ScoreManager()
    private var gameState = GameState()

    private lateinit var tvPlayer1Name: TextView
    private lateinit var tvPlayer2Name: TextView
    private lateinit var tvPlayer1Score: TextView
    private lateinit var tvPlayer2Score: TextView

    // Launcher for NamesActivity — registered before onCreate so the system can restore
    // it across process restarts. When RESULT_OK is received, both player name TextViews
    // are updated with the values chosen on the names screen.
    private val namesLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                tvPlayer1Name.text = result.data?.getStringExtra(NamesActivity.EXTRA_PLAYER_ONE_NAME)
                    ?: return@registerForActivityResult
                tvPlayer2Name.text = result.data?.getStringExtra(NamesActivity.EXTRA_PLAYER_TWO_NAME)
                    ?: return@registerForActivityResult
            }
        }

    // Sets up the screen on first creation.
    // 1. Enables edge-to-edge rendering and inflates the layout.
    // 2. Applies window insets so content sits below the status bar without covering the BottomNav.
    // 3. Binds all TextViews used for player names and scores.
    // 4. Configures the bottom navigation bar.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        tvPlayer1Name = findViewById(R.id.tvPlayer1Name)
        tvPlayer2Name = findViewById(R.id.tvPlayer2Name)
        tvPlayer1Score = findViewById(R.id.tvPlayer1Score)
        tvPlayer2Score = findViewById(R.id.tvPlayer2Score)

        setupBottomNav()
    }

    // Called when this Activity is brought back to the foreground via FLAG_ACTIVITY_SINGLE_TOP
    // (e.g. when HistoryActivity navigates back here).
    // 1. Checks for EXTRA_RESET_WINS in the new Intent.
    // 2. If present, zeroes only the win counters (scores stay untouched) and shows a Toast.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_RESET_WINS, false)) {
            gameState = scoreManager.resetWins(gameState)
            updateScoreDisplay()
            Toast.makeText(this, R.string.toast_history_reset, Toast.LENGTH_SHORT).show()
        }
    }

    // Configures the bottom navigation bar.
    // 1. Marks Score as the selected tab so it is highlighted on entry.
    // 2. Score tap returns true  → BottomNav keeps Score highlighted (already here).
    // 3. History tap returns false → BottomNav does NOT change the visual selection,
    //    so Score stays highlighted even while HistoryActivity is opening.
    // 4. Any other tab returns false (not yet implemented).
    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_score
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_score -> true
                R.id.nav_history -> { openHistory(); false }
                else -> false
            }
        }
    }

    // Delegates point addition to ScoreManager and refreshes the display.
    // 1. Passes the current state to ScoreManager.addPoints() and stores the new state.
    // 2. Updates both score TextViews with the new values.
    // 3. If the new state marks game-over, shows the game-over dialog immediately.
    private fun addPoints(player: Int, points: Int) {
        gameState = scoreManager.addPoints(gameState, player, points)
        updateScoreDisplay()
        if (gameState.isGameOver) showGameOverDialog()
    }

    // Syncs both score TextViews with the values stored in gameState.
    private fun updateScoreDisplay() {
        tvPlayer1Score.text = gameState.scorePlayerOne.toString()
        tvPlayer2Score.text = gameState.scorePlayerTwo.toString()
    }

    // Announces the winner and offers two follow-up choices.
    // 1. Resolves the winner's display name from the current TextViews.
    // 2. Builds an AlertDialog with the winner's name in the message.
    // 3. "New Game" (positive) calls startNewMatch() — resets scores, keeps win counters.
    // 4. "View History" (negative) does the same, then opens HistoryActivity.
    // 5. The dialog is non-cancellable to force a deliberate choice.
    private fun showGameOverDialog() {
        val p1Name = tvPlayer1Name.text.toString().trimEnd(':').trim()
        val p2Name = tvPlayer2Name.text.toString().trimEnd(':').trim()
        val winnerName = if (gameState.winner == 1) p1Name else p2Name

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_game_over_title)
            .setMessage(getString(R.string.dialog_game_over_message, winnerName))
            .setPositiveButton(R.string.dialog_new_game) { _, _ -> startNewMatch() }
            .setNegativeButton(R.string.dialog_view_history) { _, _ ->
                startNewMatch()
                openHistory()
            }
            .setCancelable(false)
            .show()
    }

    // Resets only the current match scores, keeping accumulated win counters intact.
    // Called automatically after the game-over dialog is dismissed.
    private fun startNewMatch() {
        gameState = scoreManager.resetMatch(gameState)
        updateScoreDisplay()
    }

    // Resets only the current match scores (Zerar Pontuação) and shows a Toast.
    // Win counters are preserved — use the Reset History button on the history screen to clear wins.
    private fun resetScore() {
        gameState = scoreManager.resetMatch(gameState)
        updateScoreDisplay()
        Toast.makeText(this, R.string.toast_score_reset, Toast.LENGTH_SHORT).show()
    }

    // Builds an Intent with the current win counts and player names, then opens HistoryActivity.
    // Win counts are read directly from gameState; names are read from the TextViews.
    private fun openHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra(HistoryActivity.EXTRA_WINS_PLAYER_ONE, gameState.winsPlayerOne)
        intent.putExtra(HistoryActivity.EXTRA_WINS_PLAYER_TWO, gameState.winsPlayerTwo)
        intent.putExtra(HistoryActivity.EXTRA_PLAYER_ONE_NAME, tvPlayer1Name.text.toString())
        intent.putExtra(HistoryActivity.EXTRA_PLAYER_TWO_NAME, tvPlayer2Name.text.toString())
        startActivity(intent)
    }

    // Builds an Intent with the current player names and launches NamesActivity for a result.
    // The names are pre-filled in the EditText fields on the names screen.
    private fun openNames() {
        val intent = Intent(this, NamesActivity::class.java)
        intent.putExtra(NamesActivity.EXTRA_PLAYER_ONE_NAME, tvPlayer1Name.text.toString())
        intent.putExtra(NamesActivity.EXTRA_PLAYER_TWO_NAME, tvPlayer2Name.text.toString())
        namesLauncher.launch(intent)
    }

    // Adds 1 tento to Player 1 (simple round win).
    fun btnPlayerOnePlus1(view: View) = addPoints(1, 1)

    // Adds 3 tentos to Player 1 (Truco).
    fun btnPlayerOnePlus3(view: View) = addPoints(1, 3)

    // Adds 6 tentos to Player 1 (Seis).
    fun btnPlayerOnePlus6(view: View) = addPoints(1, 6)

    // Adds 9 tentos to Player 1 (Nove).
    fun btnPlayerOnePlus9(view: View) = addPoints(1, 9)

    // Adds 12 tentos to Player 1 (Doze — immediately ends the match).
    fun btnPlayerOnePlus12(view: View) = addPoints(1, 12)

    // Adds 1 tento to Player 2 (simple round win).
    fun btnPlayerTwoPlus1(view: View) = addPoints(2, 1)

    // Adds 3 tentos to Player 2 (Truco).
    fun btnPlayerTwoPlus3(view: View) = addPoints(2, 3)

    // Adds 6 tentos to Player 2 (Seis).
    fun btnPlayerTwoPlus6(view: View) = addPoints(2, 6)

    // Adds 9 tentos to Player 2 (Nove).
    fun btnPlayerTwoPlus9(view: View) = addPoints(2, 9)

    // Adds 12 tentos to Player 2 (Doze — immediately ends the match).
    fun btnPlayerTwoPlus12(view: View) = addPoints(2, 12)

    // Opens the history screen via the action button in the content area.
    fun onHistory(view: View) = openHistory()

    // Resets only the current match scores (Zerar Pontuação button).
    fun onResetGame(view: View) = resetScore()

    // Opens the names screen so the user can customize both player names (Questão 4).
    fun onEditNames(view: View) = openNames()

    companion object {
        // Extra key used by HistoryActivity to signal that win counters should be zeroed.
        const val EXTRA_RESET_WINS = "reset_wins"
    }
}
