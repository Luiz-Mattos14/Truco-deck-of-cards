package br.edu.utfpr.baralhotruco

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val scoreManager = ScoreManager()
    private var gameState = GameState()

    private lateinit var tvPlayer1Name: TextView
    private lateinit var tvPlayer2Name: TextView
    private lateinit var tvPlayer1Score: TextView
    private lateinit var tvPlayer2Score: TextView

    // Initializes the Activity, sets up edge-to-edge display and binds score and name views.
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
    }

    // Delegates point addition to ScoreManager, refreshes the display
    // and shows the game over dialog if the match has ended.
    private fun addPoints(player: Int, points: Int) {
        gameState = scoreManager.addPoints(gameState, player, points)
        updateScoreDisplay()
        if (gameState.isGameOver) showGameOverDialog()
    }

    // Updates the score TextViews with the values from the current game state.
    private fun updateScoreDisplay() {
        tvPlayer1Score.text = gameState.scorePlayerOne.toString()
        tvPlayer2Score.text = gameState.scorePlayerTwo.toString()
    }

    // Shows an AlertDialog announcing the winner of the match.
    // The "New Game" button resets the state without requiring manual dialog dismissal.
    private fun showGameOverDialog() {
        val winnerName = if (gameState.winner == 1)
            tvPlayer1Name.text.toString().trimEnd(':').trim()
        else
            tvPlayer2Name.text.toString().trimEnd(':').trim()

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_game_over_title)
            .setMessage(getString(R.string.dialog_game_over_message, winnerName))
            .setPositiveButton(R.string.dialog_new_game) { _, _ -> resetGame() }
            .setCancelable(false)
            .show()
    }

    // Resets the game state and refreshes the score display.
    private fun resetGame() {
        gameState = scoreManager.reset()
        updateScoreDisplay()
    }

    // Adds 1 tento to Player 1 (simple round win).
    fun btnPlayerOnePlus1(view: View) = addPoints(1, 1)

    // Adds 3 tentos to Player 1 (Truco).
    fun btnPlayerOnePlus3(view: View) = addPoints(1, 3)

    // Adds 6 tentos to Player 1 (Seis).
    fun btnPlayerOnePlus6(view: View) = addPoints(1, 6)

    // Adds 9 tentos to Player 1 (Nove).
    fun btnPlayerOnePlus9(view: View) = addPoints(1, 9)

    // Adds 12 tentos to Player 1 (Doze — ends the match).
    fun btnPlayerOnePlus12(view: View) = addPoints(1, 12)

    // Adds 1 tento to Player 2 (simple round win).
    fun btnPlayerTwoPlus1(view: View) = addPoints(2, 1)

    // Adds 3 tentos to Player 2 (Truco).
    fun btnPlayerTwoPlus3(view: View) = addPoints(2, 3)

    // Adds 6 tentos to Player 2 (Seis).
    fun btnPlayerTwoPlus6(view: View) = addPoints(2, 6)

    // Adds 9 tentos to Player 2 (Nove).
    fun btnPlayerTwoPlus9(view: View) = addPoints(2, 9)

    // Adds 12 tentos to Player 2 (Doze — ends the match).
    fun btnPlayerTwoPlus12(view: View) = addPoints(2, 12)

    // Opens the move history screen (not yet implemented).
    fun onHistory(view: View) { }

    // Resets the current match score and history.
    fun onResetGame(view: View) = resetGame()

    // Opens the dialog to edit player names (not yet implemented).
    fun onEditNames(view: View) { }
}
