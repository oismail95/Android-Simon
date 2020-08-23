package com.example.simon

import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.game_over.*

//Here is where the results are received from the serializable class, GetResults
class GameResults(setup: GetResults){
    val points: Int
    val round: Int

    init {
        points = setup.points
        round = setup.round
    }
}

class GameOver : AppCompatActivity(){

    //This variable will be used to display the results on the user's progress
    private var results: String? = null

    private var model: GameResults? = null
    private var viewResults: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        //Here is where the results gets setup before initializing the string
        val getResults = intent.getSerializableExtra("GetResults") as? GetResults
        getResults?.let{getResults ->
            this.model = GameResults(getResults)
        }

        //Here is the process of setting the contents before the results gets displayed
        viewResults = this.findViewById(R.id.results)
        results = this.getString(R.string.displayResults, model?.points, model?.round)

        //Here is where the results gets displayed
        viewResults?.setText(results)

        //Here is where the user is prompted to access to the main screen
        backToMain()
    }

    //Here is where the user is prompted to access to the main screen
    private fun backToMain(){
        reset.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}