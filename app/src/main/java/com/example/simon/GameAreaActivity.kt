package com.example.simon

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.game_screen.*
import kotlinx.android.synthetic.main.game_screen.view.*
import kotlin.concurrent.timer
import kotlin.random.Random

class GameModel(setup: GameSetup){
    val sequence: Int
    val timeRound: Long
    val buttonSpeed: Int

    //Here is where the contents gets initialized
    init {
        sequence = setup.seq
        timeRound = setup.timeRound
        buttonSpeed = setup.buttonSpeed
    }

    //Here is where the sequence of the buttons starts to initialize whenever the game starts
    fun generateRandomFlashForDifficulty() : ArrayList<Int>{
        var sequenceCount = sequence

        var randPosNum = ArrayList<Int>()

        for (index in 0 until sequence) {
            //generate a random number between 0 and 4
            // add to the sequence array
            randPosNum.add((Math.random() * 4).toInt())
        }

        return randPosNum
    }

    //Here is where the next sequence gets added in an ArrayList whenever the next round progresses
    fun addRandomFlash(randPosNum: ArrayList<Int>) : ArrayList<Int>{
        randPosNum.add((Math.random() * 4).toInt())
        return randPosNum
    }
}

class GameWork : Fragment(){

    //Here is where the functions representing each button color
    interface ColorButton{
        fun red()
        fun blue()
        fun yellow()
        fun green()
    }

    var listener: ColorButton? = null

    //Here is where each listener of each color button sets up
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.game_screen, container, false)

        view.red_color.setOnClickListener{
            listener?.red()
        }

        view.blue_color.setOnClickListener{
            listener?.blue()
        }

        view.yellow_color.setOnClickListener{
            listener?.yellow()
        }

        view.green_color.setOnClickListener{
            listener?.green()
        }

        return view
    }

    //Here is where the sequence flashes when each round starts
    fun runSequenceWith(sequence: ArrayList<Int>, buttonSpeed: Int) {
        activity?.let {activity ->
            //Here is where the buttons gets assigned to flash in a sequence
            for (index in 0 until sequence.size) {
                val view = when (sequence[index]) {
                    0 -> this.red_color
                    1 -> this.blue_color
                    2 -> this.yellow_color
                    else -> this.green_color
                }

                //Here is where the buttons begins the process to flash
                val originalColor = view.background as? ColorDrawable
                val whiteColor = ContextCompat.getColor(activity, R.color.white)
                val animator = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    originalColor?.color,
                    whiteColor,
                    originalColor?.color
                )

                animator.addUpdateListener { valueAnimator ->
                    (valueAnimator.animatedValue as? Int)?.let {
                        view.setBackgroundColor(it)
                    }
                }

                animator?.startDelay = (600 + (index * buttonSpeed)).toLong()
                animator?.start()
            }
        }
    }

    //Here is where a button flashes whenever the user presses one of the buttons
    fun clickButton(flashButton: Int){
        activity?.let{activity ->

            //Here is where the button is assigned to flash
            val view = when(flashButton){
                0 -> this.red_color
                1 -> this.blue_color
                2 -> this.yellow_color
                else ->this.green_color
            }

            //Here is where the buttons begins the process to flash
            val originalColor = view.background as? ColorDrawable
            val whiteColor = ContextCompat.getColor(activity, R.color.white)
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                originalColor?.color,
                whiteColor,
                originalColor?.color
            )

            animator.addUpdateListener { valueAnimator ->
                (valueAnimator.animatedValue as? Int)?.let {
                    view.setBackgroundColor(it)
                }
            }

            animator?.start()
        }
    }
}

class GameAreaActivity : AppCompatActivity() {

    //Here is where the model and view gets initiaized by null
    private var model: GameModel? = null
    private var viewFragment: GameWork? = null

    //Here is where the current button position, points, and round gets initialized
    var pos: Int = 0
    var points: Int = 0
    var round: Int = 1

    //Here is where the sequence sets up after it is initialized to null
    var getRandFlash: ArrayList<Int>? = null

    /*Here is where the contents of a timer gets initialized*/
    var timeAmount: Long = 0
    //This countDownInterval, 1000 milliseconds is equivalent to 1 second
    val countDownInterval: Long = 1000
    //This is the initialization of setting the timer to 0 seconds every time the round starts
    var counter: Int = 0
    var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        //Here is where the game contents and the time amount gets initialized
        val gameSetup = intent.getSerializableExtra("GameSetup") as? GameSetup
        gameSetup?.let { gameSetup ->
            this.model = GameModel(gameSetup)
            timeAmount = 1000 * model!!.timeRound
        }

        //Here is where the view fragment gets initialized
        viewFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? GameWork
        if(viewFragment == null){
            Log.e("TAG", "View Fragment does not exist!")
            viewFragment = GameWork()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, viewFragment!!)
                .commit()
        }
        else{
            Log.e("TAG", "View Fragment exists!")
        }

        getRandFlash = model?.generateRandomFlashForDifficulty()

        //Here is where the time gets created
        this.timer = timer(timeAmount, countDownInterval).start()

        //Here is where the buttons gets responsed whenever the user presses on them
        viewFragment?.listener = object : GameWork.ColorButton{

            //Here is the red button
            override fun red() {
                //If the user hits the correct button, the button
                //will flash and points will be incremented by one
                /****************************************************/
                //Otherwise if the user hits the wrong button, the
                //correct button will flash and the game will be over
                if(getRandFlash!![pos] == 0) {
                    model?.let {
                        viewFragment?.clickButton(0)
                    }

                    points++
                }
                else {
                    //Flash on the right color
                    model?.let {
                        viewFragment?.clickButton(getRandFlash!![pos])
                    }
                    proceedGameOver(points, round)
                }

                //The next current position gets incremented
                //to move on to the next sequence
                pos++

                //If the sequence is finished, the next round moves on,
                //a new sequence button is added, a new sequence of
                //buttons are flashed, and the timer will set back to 0
                if(pos == getRandFlash!!.size) {
                    round++
                    pos = 0
                    getRandFlash = model?.addRandomFlash(getRandFlash!!)

                    model?.let {
                        viewFragment?.runSequenceWith(getRandFlash!!, model!!.buttonSpeed)
                    }

                    gameSetup?.let{
                        timeAmount = 1000 * model!!.timeRound
                    }
                    counter = 0
                    timer?.start()
                }
            }

            //Here is the blue buttom
            override fun blue() {
                //If the user hits the correct button, the button
                //will flash and points will be incremented by one
                /****************************************************/
                //Otherwise if the user hits the wrong button, the
                //correct button will flash and the game will be over
                if(getRandFlash!![pos] == 1) {
                    model?.let {
                        viewFragment?.clickButton(1)
                    }
                    points++
                }
                else {
                    //Flash on right color and display game over screen
                    model?.let {
                        viewFragment?.clickButton(getRandFlash!![pos])
                    }
                    proceedGameOver(points, round)
                }

                //The next current position gets incremented
                //to move on to the next sequence
                pos++

                //If the sequence is finished, the next round moves on,
                //a new sequence button is added, a new sequence of
                //buttons are flashed, and the timer will set back to 0
                if(pos == getRandFlash!!.size) {
                    round++
                    pos = 0
                    getRandFlash = model?.addRandomFlash(getRandFlash!!)

                    model?.let {
                        viewFragment?.runSequenceWith(getRandFlash!!, model!!.buttonSpeed)
                    }
                    gameSetup?.let{
                        timeAmount = 1000 * model!!.timeRound
                    }
                    counter = 0
                    timer?.start()
                }
            }

            //Here is the yellow button
            override fun yellow() {
                //If the user hits the correct button, the button
                //will flash and points will be incremented by one
                /****************************************************/
                //Otherwise if the user hits the wrong button, the
                //correct button will flash and the game will be over
                if(getRandFlash!![pos] == 2){
                    model?.let {
                        viewFragment?.clickButton(2)
                    }
                    points++
                }
                else {
                    //Flash on right color and display game over on screen
                    model?.let {
                        viewFragment?.clickButton(getRandFlash!![pos])
                    }
                    proceedGameOver(points, round)
                }

                //The next current position gets incremented
                //to move on to the next sequence
                pos++

                //If the sequence is finished, the next round moves on,
                //a new sequence button is added, a new sequence of
                //buttons are flashed, and the timer will set back to 0
                if(pos == getRandFlash!!.size) {
                    round++
                    pos = 0
                    getRandFlash = model?.addRandomFlash(getRandFlash!!)

                    model?.let {
                        viewFragment?.runSequenceWith(getRandFlash!!, model!!.buttonSpeed)
                    }
                    gameSetup?.let{
                        timeAmount = 1000 * model!!.timeRound
                    }
                    counter = 0
                    timer?.start()
                }
            }

            //Here is the green button
            override fun green() {
                //If the user hits the correct button, the button
                //will flash and points will be incremented by one
                /****************************************************/
                //Otherwise if the user hits the wrong button, the
                //correct button will flash and the game will be over
                if(getRandFlash!![pos] == 3) {
                    model?.let {
                        viewFragment?.clickButton(3)
                    }
                    points++
                }
                else {
                    //Flash on right color and display game over on screen
                    model?.let {
                        viewFragment?.clickButton(getRandFlash!![pos])
                    }
                    proceedGameOver(points, round)
                }

                //The next current position gets incremented
                //to move on to the next sequence
                pos++

                //If the sequence is finished, the next round moves on,
                //a new sequence button is added, a new sequence of
                //buttons are flashed, and the timer will set back to 0
                if(pos == getRandFlash!!.size) {
                    round++
                    pos = 0
                    getRandFlash = model?.addRandomFlash(getRandFlash!!)

                    model?.let {
                        viewFragment?.runSequenceWith(getRandFlash!!, model!!.buttonSpeed)
                    }
                    gameSetup?.let{
                        timeAmount = 1000 * model!!.timeRound
                    }
                    counter = 0
                    timer?.start()
                }
            }
        }

        //Here is where the output contents gets displayed
        displayOutput(getRandFlash!!)
    }

    //Here is when the timer starts every time the game starts
    override fun onStart() {
        super.onStart()
        timer?.start()
    }

    //Here is where the buttons starts to flash whenever the game begins
    override fun onResume() {
        super.onResume()
        model?.apply {
            viewFragment!!.runSequenceWith(getRandFlash!!, model!!.buttonSpeed)
        }
    }

    //Here is where the timer pauses whenever the game is over
    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }

    //This onRestart does nothing
    override fun onRestart() {
        super.onRestart()
    }

    //Here is where the app gets destroyed whenever the app closes
    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "Activity is destroyed!!")
    }

    //Here is where the timer is set every time the round starts
    private fun timer(timeAmount: Long, countDownInterval: Long) : CountDownTimer{
        return object: CountDownTimer(timeAmount, countDownInterval){

            //Here is the process of ticking the timer in seconds
            override fun onTick(millisUntilFinished: Long) {
                counter++
            }

            //Here is where the game over proceeds whenever the time is up
            override fun onFinish() {
                //Flash on the right color
                model?.let {
                    viewFragment?.clickButton(getRandFlash!![pos])
                }
                proceedGameOver(points, round)
            }
        }
    }

    //Here is where the game over gets proceeded whenever the user hits the wrong button or the timer runs out
    fun proceedGameOver(points: Int, round: Int){
        val setup = GetResults(points, round)

        val intent = Intent(this, GameOver::class.java )
        intent.putExtra("GetResults", setup)
        startActivity(intent)
    }

    //Here is to test the display of the contents for the game
    private fun displayOutput(getRandFlash: ArrayList<Int>){
        model?.apply {
            println("Sequence: $sequence, Time Round: $timeRound, Button Speed: $buttonSpeed\n")
        }
    }
}