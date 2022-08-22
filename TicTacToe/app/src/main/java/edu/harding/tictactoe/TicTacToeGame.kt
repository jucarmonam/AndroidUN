package edu.harding.tictactoe

class TicTacToeGame  {
    val HUMAN_PLAYER = 'X'
    val COMPUTER_PLAYER = 'O'
    val OPEN_SPOT = ' '
    val BOARD_SIZE = 9

    constructor(){

    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    fun clearBoard(){

    }
    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    fun setMove(player:  Char, location: Int){

    }
    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    fun getComputerMove(): Int{
        return 0
    }
    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */

    fun checkForWinner(): Int {
        return 0
    }
}