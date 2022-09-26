package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tictactoe.adapter.ItemAdapter
import com.example.tictactoe.database.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MultiplayerActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        /*
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
         */

        database = Firebase.database.reference

        val myDataset = listOf("Elbisho", "MessiasSala", "Pqr")

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = ItemAdapter(this, myDataset)
        recyclerView.setHasFixedSize(true)

        val newGameButton: Button = findViewById(R.id.new_room)
        newGameButton.setOnClickListener{
            createGame()
        }
    }

    private fun createGame(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Set a room name")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ ->
            val roomName = editText.text.toString()
            val room = Room(roomName,1)
            database.child("rooms").child(roomName).setValue(room).addOnSuccessListener {
                val intent = Intent(this, OnlineGameActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }
}