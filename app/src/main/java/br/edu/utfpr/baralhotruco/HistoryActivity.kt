package br.edu.utfpr.baralhotruco

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {

    // Sets up the screen on first creation.
    // 1. Enables edge-to-edge rendering and sets the layout.
    // 2. Applies window insets so the content sits below the status bar.
    // 3. Configures the bottom navigation bar with the History tab selected.
    // 4. Reads player names and win counts from the launching Intent.
    // 5. Populates the TextViews with the received values.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNav()

        val p1Name = intent.getStringExtra(EXTRA_PLAYER_ONE_NAME) ?: getString(R.string.player1_default)
        val p2Name = intent.getStringExtra(EXTRA_PLAYER_TWO_NAME) ?: getString(R.string.player2_default)
        val winsP1 = intent.getIntExtra(EXTRA_WINS_PLAYER_ONE, 0)
        val winsP2 = intent.getIntExtra(EXTRA_WINS_PLAYER_TWO, 0)

        findViewById<TextView>(R.id.tvPlayer1Name).text = p1Name
        findViewById<TextView>(R.id.tvPlayer1Wins).text = winsP1.toString()
        findViewById<TextView>(R.id.tvPlayer2Name).text = p2Name
        findViewById<TextView>(R.id.tvPlayer2Wins).text = winsP2.toString()
    }

    // Configures the bottom navigation bar.
    // 1. Marks History as the selected tab before setting the listener,
    //    so the assignment itself does not trigger navigation.
    // 2. History tap returns true  → BottomNav keeps History highlighted (already here).
    // 3. Score tap returns false   → BottomNav does NOT change the visual selection,
    //    so History stays highlighted while MainActivity opens.
    // 4. Any other tab returns false (not yet implemented).
    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_history
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_history -> true
                R.id.nav_score -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    false
                }
                else -> false
            }
        }
    }

    // Handles the Reset History button click.
    // 1. Builds an Intent targeting MainActivity with FLAG_ACTIVITY_CLEAR_TOP
    //    so it brings the existing instance to the foreground instead of creating a new one.
    // 2. Attaches EXTRA_RESET_WINS=true so MainActivity knows to zero the win counters.
    // 3. Starts the Intent — MainActivity's onNewIntent callback does the actual reset.
    fun onResetHistory(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(MainActivity.EXTRA_RESET_WINS, true)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_WINS_PLAYER_ONE = "wins_player_one"
        const val EXTRA_WINS_PLAYER_TWO = "wins_player_two"
        const val EXTRA_PLAYER_ONE_NAME = "player_one_name"
        const val EXTRA_PLAYER_TWO_NAME = "player_two_name"
    }
}
