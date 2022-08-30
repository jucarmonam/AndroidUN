package com.example.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import edu.harding.tictactoe.TicTacToeGame

class AndroidTicTacToeActivity : AppCompatActivity() {
    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    private lateinit var mBoardButtons : Array<Button?>

    // Various text displayed
    private lateinit var mInfoTextView: TextView

    private lateinit var mDifficultyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardButtons = arrayOfNulls(TicTacToeGame().BOARD_SIZE)
        mBoardButtons[0] = findViewById<View>(R.id.one) as Button
        mBoardButtons[1] = findViewById<View>(R.id.two) as Button
        mBoardButtons[2] = findViewById<View>(R.id.three) as Button
        mBoardButtons[3] = findViewById<View>(R.id.four) as Button
        mBoardButtons[4] = findViewById<View>(R.id.five) as Button
        mBoardButtons[5] = findViewById<View>(R.id.six) as Button
        mBoardButtons[6] = findViewById<View>(R.id.seven) as Button
        mBoardButtons[7] = findViewById<View>(R.id.eight) as Button
        mBoardButtons[8] = findViewById<View>(R.id.nine) as Button
        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mGame = TicTacToeGame()

        startNewGame()
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

        when(mGame.getDifficultyLevel()){
            TicTacToeGame.DifficultyLevel.Easy->{mDifficultyTextView.text = "The difficulty is easy"}
            TicTacToeGame.DifficultyLevel.Harder->{mDifficultyTextView.text = "The difficulty is hard"}
            TicTacToeGame.DifficultyLevel.Expert->{mDifficultyTextView.text = "The difficulty is expert"}
        }

        // Reset all buttons
        for (i in mBoardButtons.indices) {
            mBoardButtons[i]!!.text = ""
            mBoardButtons[i]!!.isEnabled = true
            mBoardButtons[i]!!.setOnClickListener{
                if (mBoardButtons[i]!!.isEnabled) {
                    setMove(TicTacToeGame().HUMAN_PLAYER, i)
                    // If no winner yet, let the computer make a move
                    var winner: Int = mGame.checkForWinner()
                    if (winner == 0) {
                        mInfoTextView.text = "It's Android's turn."
                        val move: Int = mGame.getComputerMove()
                        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                    }
                    when (winner) {
                        0 -> mInfoTextView.text =
                            "It's your turn."
                        1 -> mInfoTextView.text =
                            "It's a tie!"
                        2 -> {
                            mInfoTextView.text = "You won!"
                            blockButtons()
                            }
                        else -> {
                            mInfoTextView.text = "Android won!"
                            blockButtons()
                        }
                    }
                }
            }
        }

        // Human goes first
        mInfoTextView.text = "You go first."
    }

    private fun blockButtons(){
        for (i in mBoardButtons.indices){
            mBoardButtons[i]!!.isEnabled = false
        }
    }

    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false
        mBoardButtons[location]!!.text = player.toString()
        if (player == mGame.HUMAN_PLAYER) mBoardButtons[location]?.setTextColor(
            Color.rgb(
                0,
                200,
                0
            )
        ) else mBoardButtons[location]?.setTextColor(Color.rgb(200, 0, 0))
    }

    private fun difficultyAlert(){
        var dialog: AlertDialog
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
                        var difficulty = TicTacToeGame.DifficultyLevel.Easy
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    1 ->{
                        var difficulty = TicTacToeGame.DifficultyLevel.Harder
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    2 ->{
                        var difficulty = TicTacToeGame.DifficultyLevel.Expert
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