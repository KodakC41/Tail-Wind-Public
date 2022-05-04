package com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.FlightResponse

@Entity(tableName = "flights_list")
data class Flight(
    @PrimaryKey(autoGenerate = true)
    @TypeConverters(Converters::class)
    var id: Long = 0L,
    var ident: String = "",
    var fa_flight_id: String = "" ,
    var operator: String= "",
    var operator_iata: String = "",
    var cancelled: Boolean = false,
    var origin: String = "",
    var destination: String = "",
    var gate_origin: String = "",
    var gate_destination: String = "",
    var scheduled_out: String= "",
    var name : FlightResponse = FlightResponse())
