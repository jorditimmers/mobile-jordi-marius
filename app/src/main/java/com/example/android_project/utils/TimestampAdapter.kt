package com.example.android_project.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TimestampAdapter(private val timestamps: List<Timestamp>, private val reservedTimestamps: List<Timestamp>, private val onClick: (Timestamp) -> Unit, private val onTimestampSelected: (Timestamp) -> Unit) : RecyclerView.Adapter<TimestampAdapter.ViewHolder>() {

    var selectedItem: Timestamp? = null

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timestamp, parent, false) as TextView
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timestamp = timestamps[position]
        val formattedTimestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
        holder.textView.text = formattedTimestamp

        val currentTimestamp = Timestamp.now() // Get the current timestamp

        // Check if the timestamp is in the future
        if (timestamp.seconds > currentTimestamp.seconds) {
            holder.itemView.setOnClickListener {
                selectedItem = timestamp
                onClick(timestamp)
                onTimestampSelected(timestamp)  // Call the callback when a timestamp is selected
                notifyDataSetChanged()  // Notify the adapter to update the views
            }
            // Check if the timestamp is reserved or within 90 minutes after a reserved timestamp
            if (isReservedOrWithin90Min(timestamp)) {
                holder.textView.setTextColor(Color.RED) // Change text color to red
                holder.itemView.isClickable = false // Disable click event

            } else {
                holder.textView.setTextColor(Color.BLACK) // Change text color to black
                holder.itemView.setOnClickListener {
                    selectedItem = timestamp
                    onClick(timestamp)
                    notifyDataSetChanged() // Notify the adapter to update the views
                }
            }
        } else {
            holder.textView.setTextColor(Color.GRAY) // Change text color to gray
            holder.itemView.isClickable = false // Disable click event
        }

        // Set the selected state
        if (timestamp == selectedItem) {
            holder.textView.setBackgroundResource(R.drawable.selected_item_background) // Change the background of the selected item
        } else {
            holder.textView.setBackgroundResource(R.drawable.normal_item_background) // Change the background of the normal item
        }
    }



    override fun getItemCount() = timestamps.size

    private fun isReservedOrWithin90Min(timestamp: Timestamp): Boolean {
        // If reservedTimestamps is null or empty, return false
        if (reservedTimestamps == null || reservedTimestamps.isEmpty()) {
            return false
        }
        // Check if the timestamp is reserved
        if (isTimestampReserved(timestamp)) {
            return true
        }

        // Check if the timestamp is within 90 minutes after a reserved timestamp
        if (isWithin90MinAfterReserved(timestamp)) {
            return true
        }

        // Check if the timestamp is within 60 minutes before a reserved timestamp
        if (isWithin60MinBeforeReserved(timestamp)) {
            return true
        }

        return false  // Return false if none of the above conditions are met
    }

    private fun isWithin60MinBeforeReserved(timestamp: Timestamp): Boolean {
        val sixtyMinInMilliseconds = 60 * 60 * 1000
        for (reservedTimestamp in reservedTimestamps) {
            val diff = reservedTimestamp.toDate().time - timestamp.toDate().time
            if (diff in 0 until sixtyMinInMilliseconds) {
                return true
            }
        }
        return false
    }


    private fun isTimestampReserved(timestamp: Timestamp): Boolean {
        return reservedTimestamps.contains(timestamp)
    }

    private fun isWithin90MinAfterReserved(timestamp: Timestamp): Boolean {
        val ninetyMinInMilliseconds = 90 * 60 * 1000
        for (reservedTimestamp in reservedTimestamps) {
            val diff = timestamp.toDate().time - reservedTimestamp.toDate().time
            if (diff in 0 until ninetyMinInMilliseconds) {
                return true
            }
        }
        return false
    }




}

