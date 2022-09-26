package com.example.tictactoe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tictactoe.R

class ItemAdapter(private val context: Context, private val dataset: List<String>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val room: TextView = view.findViewById(R.id.room_name)
        val players: TextView = view.findViewById(R.id.players_count)
        val joinButton: Button = view.findViewById(R.id.join_button)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.room.text = item
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     * lo mismo que escribir esto
    override fun getItemCount(): Int {
    return dataset.size
    }
     */
    override fun getItemCount() = dataset.size
}