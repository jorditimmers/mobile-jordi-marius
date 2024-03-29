package com.example.android_project.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.R
import com.example.android_project.models.UserReservation
import java.text.SimpleDateFormat
import java.util.Locale


class ReservationListAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ReservationListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(reservation: UserReservation)

        fun onItemDetailClick(reservation: UserReservation)
    }


    private val reservations = mutableListOf<UserReservation>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clubName: TextView = view.findViewById(R.id.clubName)
        val genderType: TextView = view.findViewById(R.id.genderType)
        val matchType: TextView = view.findViewById(R.id.matchType)
        val players: TextView = view.findViewById(R.id.players)
        val reservedTimestamp: TextView = view.findViewById(R.id.reservedTimestamp)
        val navigateButton: Button = view.findViewById(R.id.navigateButton) // Assuming you have a Button in your item_reservation layout

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.clubName.text = "Club Name: ${reservation.clubName}"
        holder.matchType.text = "Match Type: ${reservation.matchType ?: "None"}"
        holder.genderType.text = "Gender Type : ${reservation.genderType ?: "None"}"
        holder.players.text = "Number Of Players: ${reservation.players?.size}"


        val timestamp = reservation.reservedTimestamp?.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = formatter.format(timestamp)
        holder.reservedTimestamp.text = "Reserved Timestamp: $dateString"

        // Show or hide the button based on the isMatch value
        if (reservation.isMatch == false) {
            holder.navigateButton.visibility = View.VISIBLE
            holder.navigateButton.setOnClickListener {
                val reservation = reservations[position]
                listener.onItemClick(reservation)
            }
        }
         else {
            holder.navigateButton.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onItemDetailClick(reservation)
        }
    }

    override fun getItemCount() = reservations.size

    fun addReservation(reservation: UserReservation) {
        reservations.add(reservation)
        reservations.sortWith(compareBy<UserReservation> { it.isMatch }.thenBy { it.reservedTimestamp })
        notifyDataSetChanged()
    }


}
