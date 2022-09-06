package com.example.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
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
import java.util.*


class AndroidTicTacToeActivity : AppCompatActivity() {
    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    //private lateinit var mBoardButtons : Array<Button?>

    // Various text displayed
    private lateinit var mInfoTextView: TextView

    private lateinit var mDifficultyTextView: TextView

    private lateinit var mBoardView: BoardView

    private var mGameOver = false

    private var playerMove = true

    var mHumanMediaPlayer: MediaPlayer? = null
    var mComputerMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mGame = TicTacToeGame()
        mBoardView = findViewById<View>(R.id.boardView) as BoardView
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener())

        startNewGame()
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
    }

    // Listen for touches on the board
    private inner class mTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            // Determine which cell was touched
            val col: Int = event!!.x.toInt() / mBoardView.getBoardCellWidth()
            val row: Int = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col

            if (playerMove && !mGameOver && setMove(TicTacToeGame().HUMAN_PLAYER, pos)){
                mHumanMediaPlayer?.start()
                var winner: Int = mGame.checkForWinner()
                if (winner == 0) {
                    mInfoTextView.text = "It's Android's turn."
                    playerMove = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        val move: Int = mGame.getComputerMove()
                        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
                        mBoardView.invalidate()
                        mComputerMediaPlayer?.start()
                        winner = mGame.checkForWinner()
                        playerMove = true

                        when (winner) {
                            0 -> mInfoTextView.text =
                                "It's your turn."
                            1 -> {
                                mInfoTextView.text = "It's a tie!"
                                mGameOver = true
                            }
                            2 -> {
                                mInfoTextView.text = "You won!"
                                mGameOver = true
                            }
                            else -> {
                                mInfoTextView.text = "Android won!"
                                mGameOver = true
                            }
                        }
                    }, 2000)
                }

                if(playerMove){
                    when (winner) {
                        0 -> mInfoTextView.text =
                            "It's your turn."
                        1 -> {
                            mInfoTextView.text = "It's a tie!"
                            mGameOver = true
                        }
                        2 -> {
                            mInfoTextView.text = "You won!"
                            mGameOver = true
                        }
                        else -> {
                            mInfoTextView.text = "Android won!"
                            mGameOver = true
                        }
                    }
                }
            }

            return false
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
            R.id.quit -> {
                quitGameAlert()
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

        when(mGame.getDifficultyLevel()){
            TicTacToeGame.DifficultyLevel.Easy->{mDifficultyTextView.text = "The difficulty is easy"}
            TicTacToeGame.DifficultyLevel.Harder->{mDifficultyTextView.text = "The difficulty is hard"}
            TicTacToeGame.DifficultyLevel.Expert->{mDifficultyTextView.text = "The difficulty is expert"}
        }

        // Human goes first
        mInfoTextView.text = "You go first."
    }

    /*
    private fun blockButtons(){
        for (i in mBoardButtons.indices){
            mBoardButtons[i]!!.isEnabled = false
        }
    }
     */

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
            setTitle(R.string.difficulty_choose);
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

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        finish()
    }

    private val negativeButtonClick = { _: DialogInterface, _: Int ->
        Toast.makeText(applicationContext,"Continuemos jugando", Toast.LENGTH_SHORT).show()
    }

    private fun quitGameAlert(){
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Cerrar juego")
            setMessage("Desea cerrar el juego?")
            setPositiveButton("Si", positiveButtonClick)
            setNegativeButton("No", negativeButtonClick)
            show()
        }
    }
}