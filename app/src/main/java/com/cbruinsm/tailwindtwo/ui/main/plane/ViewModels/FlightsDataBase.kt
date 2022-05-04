package com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight.MyFlights
import com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight.Flight as flights

class flightsDataBase(application: Application) : AndroidViewModel(application) {
    init {
        MyFlights.initialize(application)
    }

    private val myFriends = MyFlights.get()
    val forRecyclerView = myFriends.getAllItems()

    fun getItem(id: String) = myFriends.getItem(id)

    fun addFlight(newbuddy: flights) {
        myFriends.addItem(newbuddy)
    }

    fun deleteFlight(oldbuddy: flights) {
        myFriends.deleteItem(oldbuddy)
    }

    fun updateFlight(keeps : flights) {
       myFriends.updateItem(keeps)
    }
}