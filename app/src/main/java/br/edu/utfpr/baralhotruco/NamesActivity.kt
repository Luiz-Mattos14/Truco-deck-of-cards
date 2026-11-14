package br.edu.utfpr.baralhotruco

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class NamesActivity : AppCompatActivity() {

    private lateinit var etPlayer1Name: EditText
    private lateinit var etPlayer2Name: EditText

    // Sets up the screen on first creation.
    // 1. Enables edge-to-edge rendering and inflates the layout.
    // 2. Applies window insets so the keyboard does not cover the form fields.
    // 3. Binds the EditText views for both player names.
    // 4. Pre-fills the fields with the names received from MainActivity.
    // 5. Configures the bottom navigation bar.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_names)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        etPlayer1Name = findViewById(R.id.etPlayer1Name)
        etPlayer2Name = findViewById(R.id.etPlayer2Name)

        val p1 = intent.getStringExtra(EXTRA_PLAYER_ONE_NAME) ?: getString(R.string.player1_default)
        val p2 = intent.getStringExtra(EXTRA_PLAYER_TWO_NAME) ?: getString(R.string.player2_default)
        etPlayer1Name.setText(p1.trimEnd(':').trim())
        etPlayer2Name.setText(p2.trimEnd(':').trim())

        setupBottomNav()
    }

    // Configures the bottom navigation bar.
    // 1. Marks the Score tab as selected — this screen is part of the main game setup.
    // 2. Score tap navigates back to MainActivity clearing the back stack.
    // 3. History tap navigates to HistoryActivity.
    // 4. Both navigation taps return false so the visual selection does not change.
    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_score
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_score -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    false
                }
                R.id.nav_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    false
                }
                else -> false
            }
        }
    }

    // Handles the Confirm button click.
    // 1. Reads both EditText values and trims whitespace.
    // 2. Falls back to the default string when the field is left empty.
    // 3. Appends a colon so the name matches the display format used on the main screen.
    // 4. Puts both names in the result Intent and sets RESULT_OK.
    // 5. Finishes the Activity so MainActivity receives the result via its launcher.
    fun onConfirm(view: View) {
        val name1 = etPlayer1Name.text.toString().trim()
        val name2 = etPlayer2Name.text.toString().trim()

        val result1 = if (name1.isNotEmpty()) "$name1:" else getString(R.string.player1_default)
        val result2 = if (name2.isNotEmpty()) "$name2:" else getString(R.string.player2_default)

        val intent = Intent()
        intent.putExtra(EXTRA_PLAYER_ONE_NAME, result1)
        intent.putExtra(EXTRA_PLAYER_TWO_NAME, result2)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val EXTRA_PLAYER_ONE_NAME = "player_one_name"
        const val EXTRA_PLAYER_TWO_NAME = "player_two_name"
    }
}
