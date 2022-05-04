package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Tracker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TrackFlight {
    @GET("flights/{id}/track")
    fun getTrack(
        @Path("id") id : String,
        @Header("x-apikey") xapikey: String,
    ): Call<FlightTracker>
}