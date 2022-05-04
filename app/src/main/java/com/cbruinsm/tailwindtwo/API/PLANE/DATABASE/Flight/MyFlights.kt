package com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.util.concurrent.Executors


class MyFlights private constructor(context: Context) {

    private val database: FlightList = Room.databaseBuilder(
        context.applicationContext,
        FlightList::class.java,
        "MyFlights_List"
    ).addMigrations(migration_1_3)
        .build()

    private val flightDao = database.FlightDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addItem(flights: Flight) {
        executor.execute {
            flightDao.Add_Flight(flights)
        }
    }

    fun updateItem(flights: Flight) {
        executor.execute {
            flightDao.Flight_Change(flights)
        }
    }

    fun deleteItem(flights: Flight) {
        executor.execute {
            flightDao.RemoveFlight(flights)
        }
    }

    fun getItem(id: String): LiveData<Flight?> = flightDao.getFlight(id)

    fun getAllItems(): LiveData<List<Flight>> = flightDao.getAllFlights()

    companion object {

        private var INSTANCE: MyFlights? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MyFlights(context)
            }
        }

        fun get(): MyFlights {
            return INSTANCE
                ?: throw IllegalStateException("Flight Group must be initialized.")
        }
    }
}