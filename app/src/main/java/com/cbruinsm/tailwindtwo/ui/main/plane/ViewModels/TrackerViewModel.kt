package com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Tracker.TrackFlight
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Tracker.FlightTracker
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Tracker.positions
import com.cbruinsm.tailwindtwo.BuildConfig.FLIGHTAWARE_API
import com.cbruinsm.tailwindtwo.BuildConfig.BASE_URL
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * planesViewModel gets the plane query and gets the flight being queried.
 */
class trackerViewModel(application: Application) : AndroidViewModel(application) {
    var success : Boolean = false
    companion object {
        private val flightApi: TrackFlight by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return@lazy retrofit.create(TrackFlight::class.java)
        }
    }

    var flight = MutableLiveData<ArrayList<positions>>()
    var flightX : LiveData<ArrayList<positions>> = flight
    fun getPlanes(id: String) {
        val planeSearch: Call<FlightTracker> = flightApi.getTrack(id,FLIGHTAWARE_API)
        planeSearch.enqueue(object : Callback<FlightTracker>{
            override fun onResponse(call: Call<FlightTracker>, response: Response<FlightTracker>) {
                Log.d("getTracker()", "Response JSON: "+ Gson().toJson(response.body()) )
                if(response.isSuccessful) {
                    Log.d("getTracker()", "Response JSON: "+ Gson().toJson(response.body()) )
                    response.body()?.positions?.let {
                        if(it.size > 0) {
                            flight.value = it
                            success = true
                        } else {
                            success = false
                        }
                    }
                }
            }
            override fun onFailure(call: Call<FlightTracker>, t: Throwable) {
                Log.d("Planes", "failed to communicate with API.")
            }
        })
    }
}