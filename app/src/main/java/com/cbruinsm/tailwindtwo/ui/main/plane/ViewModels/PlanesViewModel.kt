package com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data.FlightAware
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data.FlightData
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data.flights as flights
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
class planesViewModel(application: Application) : AndroidViewModel(application) {
    var success : Boolean = false
    companion object {
        private val flightApi: FlightData by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return@lazy retrofit.create(FlightData::class.java)
        }
    }

    var flight = MutableLiveData<ArrayList<flights>>()
    var flightX : LiveData<ArrayList<flights>> = flight
    fun getPlanes(ident: String) {
        val planeSearch: Call<FlightAware> = flightApi.getPlane(ident,FLIGHTAWARE_API)
        planeSearch.enqueue(object : Callback<FlightAware>{
            override fun onResponse(call: Call<FlightAware>, response: Response<FlightAware>) {
                if(response.isSuccessful) {
                    Log.d("getPlanes()", "Response JSON: "+ Gson().toJson(response.body()) )
                    response.body()?.flights?.let {
                        if(it.size > 0) {
                            flight.value = it
                            success = true
                        }
                        else {
                            success = false
                        }
                    }
                }
            }
            override fun onFailure(call: Call<FlightAware>, t: Throwable) {
                Log.d("Planes", "failed to communicate with API.")
            }
        })
    }
}