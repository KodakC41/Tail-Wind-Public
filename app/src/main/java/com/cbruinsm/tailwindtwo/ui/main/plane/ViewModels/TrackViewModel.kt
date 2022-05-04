package com.cbruinsm.tailwindtwo.ui.main.plane.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.fixes
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.FlightFocus
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.Tracker
import com.cbruinsm.tailwindtwo.BuildConfig.FLIGHTAWARE_API
import com.cbruinsm.tailwindtwo.BuildConfig.BASE_URL
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * trackViewModel gets the plane query and gets the flight being queried.
 */
class trackViewModel : ViewModel() {
    var success : Boolean = false

    companion object {
        private val trackerApi: Tracker by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return@lazy retrofit.create(Tracker::class.java)
        }
    }

    var _route = MutableLiveData<ArrayList<fixes>>()
    var route : LiveData<ArrayList<fixes>> = _route
    /**
     * Get's the position of the airports? Maybe something else instead
     */
    fun getPos(id: String) {
        val airportSearch: Call<FlightFocus> = trackerApi.getPort(id,FLIGHTAWARE_API)
        airportSearch.enqueue(object : Callback<FlightFocus>{
            override fun onResponse(call: Call<FlightFocus>, response: Response<FlightFocus>) {
                if(response.isSuccessful) {
                    Log.d("getTrack()", "Response JSON: "+ Gson().toJson(response.body()) )
                    success = true
                    response.body()?.fixes?.let {
                        _route.value = it
                    }
                }
            }
            override fun onFailure(call: Call<FlightFocus>, t: Throwable) {
                Log.d("Tracker", "failed to communicate with API.")
            }
        })
    }
}