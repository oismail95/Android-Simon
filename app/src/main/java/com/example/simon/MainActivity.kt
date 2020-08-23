package com.example.simon

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.game_screen.*
import kotlinx.android.synthetic.main.game_screen.view.*

class MainActivity : AppCompatActivity() {

    var setup = GameAreaActivity()
    private var modelFragment: GameAreaActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diffButtons()
    }

    //Here is where the button gets accessed when the user chooses the difficulty
    fun diffButtons() {

        //Access to easy difficulty
        easy_button.setOnClickListener {
            //Here is where the contents set up for the game
            val setup = GameSetup(1, 999, 600)

            //Here is where the game screen gets accessed using intent
            val intent = Intent(this, GameAreaActivity::class.java)
            intent.putExtra("GameSetup", setup)
            startActivity(intent)
        }

        //Access to medium difficulty
        medium_button.setOnClickListener {
            //Here is where the contents set up for the game
            val setup = GameSetup(3, 60, 400)

            //Here is where the game screen gets accessed using intent
            val intent = Intent(this, GameAreaActivity::class.java)
            intent.putExtra("GameSetup", setup)
            startActivity(intent)
        }

        //Access to hard difficulty
        hard_button.setOnClickListener {
            //Here is where the contents set up for the game
            val setup = GameSetup(5, 30, 200)

            //Here is where the game screen gets accessed using intent
            val intent = Intent(this, GameAreaActivity::class.java)
            intent.putExtra("GameSetup", setup)
            startActivity(intent)
        }
    }
}
