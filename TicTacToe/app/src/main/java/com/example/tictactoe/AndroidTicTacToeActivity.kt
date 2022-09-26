package com.example.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import edu.harding.tictactoe.BoardView
import edu.harding.tictactoe.TicTacToeGame


class AndroidTicTacToeActivity : AppCompatActivity() {
    private lateinit var mGame: TicTacToeGame

    // Various text displayed
    private lateinit var mInfoTextView: TextView

    private lateinit var mDifficultyTextView: TextView

    private lateinit var mHumanScoreTextView: TextView

    private lateinit var mComputerScoreTextView: TextView

    private lateinit var mTieScoreTextView: TextView

    private lateinit var mBoardView: BoardView

    private var mGameOver = false

    private var playerMove = true

    private var mHumanWins = 0

    private var mComputerWins = 0

    private var mTies = 0

    private var mPrefs: SharedPreferences? = null

    var mHumanMediaPlayer: MediaPlayer? = null

    private var mComputerMediaPlayer: MediaPlayer? = null

    private var playerStart: Boolean = true

    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mHumanScoreTextView = findViewById<View>(R.id.human) as TextView
        mComputerScoreTextView = findViewById<View>(R.id.android) as TextView
        mTieScoreTextView = findViewById<View>(R.id.ties) as TextView
        mGame = TicTacToeGame()
        mBoardView = findViewById<View>(R.id.boardView) as BoardView
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener())

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)
        // Restore the scores
        mHumanWins = mPrefs!!.getInt("mHumanWins", 0)
        mComputerWins = mPrefs!!.getInt("mComputerWins", 0)
        mTies = mPrefs!!.getInt("mTies", 0)

        if (savedInstanceState == null) {
            startNewGame()
        }

        displayScores()
    }

    override fun onStop() {
        super.onStop()
        // Save the current scores
        val ed = mPrefs!!.edit()
        ed.putInt("mHumanWins", mHumanWins)
        ed.putInt("mComputerWins", mComputerWins)
        ed.putInt("mTies", mTies)
        ed.apply()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mGame.setBoardState(savedInstanceState.getCharArray("board")!!)
        mGameOver = savedInstanceState.getBoolean("mGameOver")
        playerMove = savedInstanceState.getBoolean("playerMove")
        if(!playerMove){
            computerMove()
        }
        mInfoTextView.text = savedInstanceState.getCharSequence("info")
        mDifficultyTextView.text = savedInstanceState.getCharSequence("difficulty")
        playerStart = savedInstanceState.getBoolean("playerStart")

        val difficult = savedInstanceState.getCharArray("DifficultyLevel")

        if(difficult.toString() == "Easy"){
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
        }else if(difficult.toString() == "Harder"){
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
        }else if(difficult.toString() == "Expert"){
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
        }
    }

    private fun displayScores() {
        mHumanScoreTextView.text = "Human: ".plus(mHumanWins.toString())
        mComputerScoreTextView.text = "Android: ".plus(mComputerWins.toString())
        mTieScoreTextView.text = "Ties: ".plus(mTies.toString())
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.humansound)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computersound)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer?.release()
        mComputerMediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("board", mGame.getBoardState())
        outState.putBoolean("mGameOver", mGameOver)
        outState.putBoolean("playerMove", playerMove)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putCharSequence("difficulty", mDifficultyTextView.text)
        outState.putBoolean("playerStart", playerStart)
        outState.putCharSequence("DifficultyLevel", mGame.getDifficultyLevel().toString())
    }

    // Listen for touches on the board
    private inner class mTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            // Determine which cell was touched
            val col: Int = event!!.x.toInt() / mBoardView.getBoardCellWidth()
            val row: Int = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col

            if (playerMove && !mGameOver && setMove(TicTacToeGame().HUMAN_PLAYER, pos)){
                try {
                    mHumanMediaPlayer?.start()
                }catch (e: IllegalStateException){
                    println("danado")
                }
                val winner: Int = mGame.checkForWinner()
                if (winner == 0) {
                    mInfoTextView.text = "It's Android's turn."
                    playerMove = false
                    computerMove()
                }

                if(playerMove){
                    validateWinner(winner)
                }
            }

            return false
        }
    }

    private fun computerMove() {
        handler.postDelayed({
            val move: Int = mGame.getComputerMove()
            setMove(TicTacToeGame().COMPUTER_PLAYER, move)
            mBoardView.invalidate()
            try{
                mComputerMediaPlayer?.start()
            }catch (e: IllegalStateException){
                println("danado")
            }
            val winner = mGame.checkForWinner()
            playerMove = true

            validateWinner(winner)
        }, 2000)
    }

    private fun validateWinner(winner: Int) {
        when (winner) {
            0 -> mInfoTextView.text =
                "It's your turn."
            1 -> {
                mInfoTextView.text = "It's a tie!"
                mGameOver = true
                mTies++
                mTieScoreTextView.text = "Ties: ".plus(mTies)
            }
            2 -> {
                mInfoTextView.text = "You won!"
                mGameOver = true
                mHumanWins++
                mHumanScoreTextView.text = "Human: ".plus(mHumanWins)
            }
            else -> {
                mInfoTextView.text = "Android won!"
                mGameOver = true
                mComputerWins++
                mComputerScoreTextView.text = "Android: ".plus(mComputerWins)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu is MenuBuilder) {
            (menu).setOptionalIconsVisible(true)
        }
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_game -> {
                startNewGame()
                return true
            }
            R.id.ai_difficulty -> {
                difficultyAlert()
                return true
            }
            R.id.reset_scores -> {
                resetScoresAlert()
                return true
            }
            R.id.multiplayer -> {
                val intent = Intent(this, MultiplayerActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

    // Set up the game board.
    private fun  startNewGame() {
        mGame.clearBoard()
        mBoardView.invalidate()
        mGameOver = false
        handler.removeCallbacksAndMessages(null)

        when(mGame.getDifficultyLevel()){
            TicTacToeGame.DifficultyLevel.Easy->{mDifficultyTextView.text = "The difficulty is easy"}
            TicTacToeGame.DifficultyLevel.Harder->{mDifficultyTextView.text = "The difficulty is hard"}
            TicTacToeGame.DifficultyLevel.Expert->{mDifficultyTextView.text = "The difficulty is expert"}
        }

        if(!playerStart){
            mInfoTextView.text = "Android's go first."
            playerMove = false
            computerMove()
        }else{
            // Human goes first
            mInfoTextView.text = "You go first."
        }

        playerStart = !playerStart
    }

    private fun setMove(player: Char, location: Int) : Boolean {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate() // Redraw the board
            return true
        }
        return false
    }

    private fun difficultyAlert(){
        val dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val levels = arrayOf<CharSequence>(
            resources.getString(R.string.difficulty_easy),
            resources.getString(R.string.difficulty_harder),
            resources.getString(R.string.difficulty_expert)
        )
        with(builder)
        {
            setTitle(R.string.difficulty_choose)
            setSingleChoiceItems(levels, -1) { dialogInterface, item ->
                when(item){
                    0 ->{
                        val difficulty = TicTacToeGame.DifficultyLevel.Easy
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    1 ->{
                        val difficulty = TicTacToeGame.DifficultyLevel.Harder
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    2 ->{
                        val difficulty = TicTacToeGame.DifficultyLevel.Expert
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                }
                // Display the selected difficulty level
                Toast.makeText(applicationContext, levels[item], Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
        }
        dialog = builder.create()
        // Finally, display the alert dialog
        dialog.show()
    }

    private fun resetScoresAlert(){
        mTies = 0
        mHumanWins = 0
        mComputerWins = 0
        mTieScoreTextView.text = "Ties: ".plus("0")
        mHumanScoreTextView.text = "Human: ".plus("0")
        mComputerScoreTextView.text = "Android: ".plus("0")
    }
}