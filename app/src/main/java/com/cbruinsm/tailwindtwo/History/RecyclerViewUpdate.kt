package com.cbruinsm.tailwindtwo.History

import android.annotation.SuppressLint
import android.text.TextUtils.substring
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight.Flight
import com.cbruinsm.tailwindtwo.databinding.FragmentFlightsCard2Binding
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RecyclerViewUpdate :  RecyclerView.Adapter<RecyclerViewUpdate.MediaViewHolder>() {

    class MediaViewHolder(private val binding: FragmentFlightsCard2Binding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(flights: Flight) {
            binding.apply {
                FlightNum.text = flights.ident
                Depart.text = flights.origin
                Arrive.text = flights.destination
                Time.text = timeAdjust(flights.scheduled_out)
            }
        }

        private fun timeAdjust(Time : String)  : String {
            val DATE_FORMAT =
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val time =
                LocalDateTime.parse(substring(Time, 0, Time.length), DateTimeFormatter.ofPattern(DATE_FORMAT))
            val zone =
                substring(time.atZone(ZoneId.of(TimeZone.getDefault().id)).toString(),11,22)
            var TimeX =
                LocalTime.of(Integer.parseInt(substring(zone,0,2)),Integer.parseInt(substring(zone,3,5)))

            when(zone.get(5)) {

                '-' -> TimeX = TimeX.minusHours(
                        Integer.parseInt(
                            substring(
                                zone,
                                7,
                                8
                            )
                        ).toLong())

                '+' -> TimeX = TimeX.plusHours(
                        Integer.parseInt(
                            substring(
                                 zone,
                                7,
                                8
                            )
                        ).toLong())
            }

            val out = StringBuilder("Departs $TimeX local time")
            return out.toString()
        }

    }

    private var flights: List<Flight> = emptyList()
    // Creates the view for the card Recycler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MediaViewHolder(
        FragmentFlightsCard2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

    )
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(flights[position])
    }

    // Update upon Addition or Subtraction
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<Flight>) {
        this.flights = newItems
        notifyDataSetChanged()
    }
    fun getFriendAtPosition(position: Int) = flights[position]

    override fun getItemCount(): Int {
        return flights.size
    }



}

