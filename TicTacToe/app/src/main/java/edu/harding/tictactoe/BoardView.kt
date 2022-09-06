package edu.harding.tictactoe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.tictactoe.R


class BoardView : View {
    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint
    private lateinit var mGame: TicTacToeGame

    constructor(context: Context?) : super(context){
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ){
        initialize()
    }

    fun setGame(game: TicTacToeGame) {
        mGame = game
    }

    private fun initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.neonx)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.neono)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Determine the width and height of the View
        val boardWidth = width
        val boardHeight = height

        // Make thick, light gray lines
        mPaint.color = Color.LTGRAY
        mPaint.strokeWidth = 20F

        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3
        canvas.drawLine(cellWidth.toFloat(), 0F, cellWidth.toFloat(), boardHeight.toFloat(),
            mPaint
        )
        canvas.drawLine((cellWidth * 2).toFloat(), 0F, (cellWidth * 2).toFloat(),
            boardHeight.toFloat(), mPaint
        )

        // Draw the two horizontal board lines
        val cellHeight = boardHeight / 3
        canvas.drawLine(0F, cellHeight.toFloat(), boardWidth.toFloat(), cellHeight.toFloat(),
            mPaint
        )
        canvas.drawLine(0F, (cellHeight * 2).toFloat(), boardWidth.toFloat(), (cellHeight * 2).toFloat(),
            mPaint
        )

        for (i in 0 until TicTacToeGame().BOARD_SIZE) {
            val col = i % 3
            val row = i / 3

            // Define the boundaries of a destination rectangle for the image
            val left = col * cellWidth
            val top = row *  cellHeight
            val right = cellWidth * (col + 1)
            val bottom = cellHeight * (row + 1)

            if (mGame.getBoardOccupant(i) == TicTacToeGame().HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                    null, // src
                    Rect(left, top, right, bottom), // dest
                null)

            } else if (mGame.getBoardOccupant(i) == TicTacToeGame().COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                    null, // src
                    Rect(left, top, right, bottom), // dest
                null)
            }
        }
    }

}