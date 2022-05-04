package com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FlightDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun Add_Flight(flights: Flight)

        @Update
        fun Flight_Change(flights: Flight)

        @Delete
        fun RemoveFlight(flights: Flight)

        @Query("SELECT * FROM flights_list WHERE ident=(:id)")
        fun getFlight(id: String): LiveData<Flight?>

        @Query("SELECT * FROM flights_list ORDER BY ident DESC")
        fun getAllFlights(): LiveData<List<Flight>>

        @Query("SELECT * FROM flights_list ORDER BY ident DESC LIMIT 1")
        fun getLastFlight(): LiveData<Flight?>
 }