package br.edu.utfpr.baralhotruco

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    fun btnPlayerOnePlus1(view: View) { }
    fun btnPlayerOnePlus3(view: View) { }
    fun btnPlayerOnePlus6(view: View) { }
    fun btnPlayerOnePlus9(view: View) { }
    fun btnPlayerOnePlus12(view: View) { }

    fun btnPlayerTwoPlus1(view: View) { }
    fun btnPlayerTwoPlus3(view: View) { }
    fun btnPlayerTwoPlus6(view: View) { }
    fun btnPlayerTwoPlus9(view: View) { }
    fun btnPlayerTwoPlus12(view: View) { }

    fun onHistorico(view: View) { }
    fun onZerarPartida(view: View) { }
    fun onEditarNomes(view: View) { }

}
