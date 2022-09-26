package com.example.tictactoe

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.harding.tictactoe.BoardView
import edu.harding.tictactoe.TicTacToeGame

class OnlineGameActivity : AppCompatActivity() {
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

    private var mOpponentWins = 0

    private var mTies = 0

    var mHumanMediaPlayer: MediaPlayer? = null

    private var mComputerMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game)

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mHumanScoreTextView = findViewById<View>(R.id.human) as TextView
        mComputerScoreTextView = findViewById<View>(R.id.android) as TextView
        mTieScoreTextView = findViewById<View>(R.id.ties) as TextView
        mGame = TicTacToeGame()
        mBoardView = findViewById<View>(R.id.boardView) as BoardView
        mBoardView.setGame(mGame)
        mBoardView.setOnTouchListener(mTouchListener())
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
                    mInfoTextView.text = "It's Opponents's turn."
                    playerMove = false
                    opponentMove()
                }

                if(playerMove){
                    validateWinner(winner)
                }
            }

            return false
        }
    }

    private fun opponentMove() {
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
    }

    private fun setMove(player: Char, location: Int) : Boolean {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate() // Redraw the board
            return true
        }
        return false
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
                mHumanScoreTextView.text = "You: ".plus(mHumanWins)
            }
            else -> {
                mInfoTextView.text = "Opponent won!"
                mGameOver = true
                mOpponentWins++
                mComputerScoreTextView.text = "Opponent: ".plus(mOpponentWins)
            }
        }
    }
}