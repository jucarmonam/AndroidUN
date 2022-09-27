package com.example.tictactoe

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoe.database.Room
import com.example.tictactoe.databinding.ActivityOnlineGameBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import edu.harding.tictactoe.TicTacToeGame


class OnlineGameActivity : AppCompatActivity() {

    private var roomName = ""

    private lateinit var binding: ActivityOnlineGameBinding

    private lateinit var mGame: TicTacToeGame

    private var mGameOver = false

    private var player = -1

    private var mHumanWins = 0

    private var mOpponentWins = 0

    private var playerMove = true

    private var mTies = 0

    var mHumanMediaPlayer: MediaPlayer? = null

    private var mComputerMediaPlayer: MediaPlayer? = null

    private var playerTurn: Int = 0

    private var host = true

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mGame = TicTacToeGame()

        binding.boardViewOnline.setGame(mGame)
        binding.boardViewOnline.setOnTouchListener(mTouchListener())

        roomName = intent.getStringExtra("roomName").toString()
        player = intent.getIntExtra("player", 0)

        database = Firebase.database.getReference("rooms")

        if(player == 1){
            database.child(roomName).child("players").setValue(2)
            host = false
        }

        database.child(roomName).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val room = dataSnapshot.getValue<Room>()
                playerTurn = room?.playerTurn!!
                if(playerTurn == player && room.lastMove!! > -1){
                    opponentMove(room.lastMove)
                }else if(room.lastMove == -2){
                    player = if (player == 1) 0 else 1
                    startNewGame()
                }
                //adapter.notifyDataSetChanged()
                //Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            database.child(roomName).child("lastMove").setValue(-2)
            database.child(roomName).child("playerTurn").setValue(0)
        }

        startNewGame()

        displayScores()
    }

    override fun onStop() {
        super.onStop() // Always call the superclass method first
        if(host){
            database.child(roomName).removeValue()
            val intent = Intent(this, MultiplayerActivity::class.java)
            startActivity(intent)
        }else{
            database.child(roomName).child("players").setValue(1)
        }
    }

    // Set up the game board.
    private fun startNewGame() {
        binding.playButton.isEnabled = false
        mGame.clearBoard()
        binding.boardViewOnline.invalidate()
        mGameOver = false

        if(player == 1){
            binding.informationOnline.text = "Opponent go first."
            playerMove = false
        }else{
            binding.informationOnline.text = "You go first."
            playerMove = true
        }
    }

    private fun displayScores() {
        binding.you.text = "You: ".plus(mHumanWins.toString())
        binding.opponent.text = "Opponent: ".plus(mOpponentWins.toString())
        binding.tiesOnline.text = "Ties: ".plus(mTies.toString())
    }

    // Listen for touches on the board
    private inner class mTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            // Determine which cell was touched
            val col: Int = event!!.x.toInt() / binding.boardViewOnline.getBoardCellWidth()
            val row: Int = event.y.toInt() / binding.boardViewOnline.getBoardCellHeight()
            val pos = row * 3 + col

            if (playerMove && !mGameOver && setMove(TicTacToeGame().HUMAN_PLAYER, pos)){
                try {
                    mHumanMediaPlayer?.start()
                }catch (e: IllegalStateException){
                    println("danado")
                }
                val winner: Int = mGame.checkForWinner()
                if (winner == 0) {
                    binding.informationOnline.text = "It's Opponents's turn."
                    playerMove = false
                }

                if(playerMove){
                    validateWinner(winner)
                }
                playerTurn = if(playerTurn == 1) 0 else 1
                updateDatabase(pos)
            }

            return false
        }
    }

    private fun updateDatabase(pos: Int){
        database.child(roomName).child("playerTurn").setValue(playerTurn)
        database.child(roomName).child("lastMove").setValue(pos)
    }

    private fun opponentMove(pos: Int) {
        setMove(TicTacToeGame().COMPUTER_PLAYER, pos)
        binding.boardViewOnline.invalidate()
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
            binding.boardViewOnline.invalidate() // Redraw the board
            return true
        }
        return false
    }

    private fun validateWinner(winner: Int) {
        when (winner) {
            0 -> binding.informationOnline.text =
                "It's your turn."
            1 -> {
                binding.informationOnline.text = "It's a tie!"
                mGameOver = true
                mTies++
                binding.tiesOnline.text = "Ties: ".plus(mTies)
                binding.playButton.isEnabled = true
            }
            2 -> {
                binding.informationOnline.text = "You won!"
                mGameOver = true
                mHumanWins++
                binding.you.text = "You: ".plus(mHumanWins)
                binding.playButton.isEnabled = true
            }
            else -> {
                binding.informationOnline.text = "Opponent won!"
                mGameOver = true
                mOpponentWins++
                binding.opponent.text = "Opponent: ".plus(mOpponentWins)
                binding.playButton.isEnabled = true
            }
        }
    }
}