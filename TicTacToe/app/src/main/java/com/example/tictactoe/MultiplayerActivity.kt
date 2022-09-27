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
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MultiplayerActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    var myDataset = mutableListOf<Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        database = Firebase.database.reference

        val adapter = ItemAdapter(this, myDataset)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        /*
        database.child("rooms").get().addOnCompleteListener {
            if (it.isSuccessful) {
                //val myDataset = it.result.getValue<List<Room>>()
                myDataset = it.result.children.mapNotNull { doc ->
                    doc.getValue(Room::class.java)
                } as MutableList<Room>

                adapter = ItemAdapter(this, myDataset)
                recyclerView.adapter = adapter
                //Log.d(TAG, products.toString())
            } else {
                //Log.d(TAG, it.exception?.message.toString())
            }
        }
         */

        // Read from the database
        database.child("rooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                myDataset.clear()
                val values = dataSnapshot.children.mapNotNull { doc ->
                    doc.getValue(Room::class.java)
                }

                myDataset.addAll(values)

                adapter.notifyDataSetChanged()
                //Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read valueronald
                //Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

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
            val room = Room(roomName,1,0,-1)
            database.child("rooms").child(roomName).setValue(room).addOnSuccessListener {
                val intent = Intent(this, OnlineGameActivity::class.java)
                intent.putExtra("roomName", roomName)
                intent.putExtra("player", 0)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }
}