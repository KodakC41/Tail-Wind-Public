package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface Tracker {
    @GET("flights/{id}/route")
    fun getPort(
        @Path("id") id : String,
        @Header("x-apikey") xapikey: String,
    ):  Call<FlightFocus>
}