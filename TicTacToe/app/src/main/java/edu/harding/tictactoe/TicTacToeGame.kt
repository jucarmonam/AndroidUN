package edu.harding.tictactoe

import kotlin.random.Random

class TicTacToeGame {
    val HUMAN_PLAYER = 'X'
    val COMPUTER_PLAYER = 'O'
    val OPEN_SPOT = ' '
    val BOARD_SIZE = 9
    private var mBoard = charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ')

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    fun clearBoard(){
        mBoard = charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ')
    }
    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    fun setMove(player:  Char, location: Int){
        mBoard[location] = player;
    }
    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    fun getComputerMove(): Int{
        var move: Int

        // First see if there's a move O can make to win

        // First see if there's a move O can make to win
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i]
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    return i
                } else mBoard[i] = curr
            }
        }

        // See if there's a move O can make to block X from winning

        // See if there's a move O can make to block X from winning
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i] // Save the current number
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER
                    return i
                } else mBoard[i] = curr
            }
        }

        // Generate random move
        do {
            move = Random.nextInt(BOARD_SIZE)
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER)

        mBoard[move] = COMPUTER_PLAYER
        return move
    }
    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */

    fun checkForWinner(): Int {
        // Check horizontal wins
        for (i in 0..6 step 3) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 1] == HUMAN_PLAYER && mBoard[i + 2] == HUMAN_PLAYER) {
                return 2
            }
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 1] == COMPUTER_PLAYER && mBoard[i + 2] == COMPUTER_PLAYER) {
                return 3
            }
        }
        // Check vertical wins
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 3] == HUMAN_PLAYER && mBoard[i + 6] == HUMAN_PLAYER) {
                return 2
            }
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 3] == COMPUTER_PLAYER && mBoard[i + 6] == COMPUTER_PLAYER) {
                return 3
            }
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER) ||
            (mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER)){
            return 2
        }

        if ((mBoard[0] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER) ||
            (mBoard[2] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER)){
            return 3
        }

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER){
                return 0
            }
        }
        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }
}