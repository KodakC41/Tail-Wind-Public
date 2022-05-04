package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


/**
 * Flight Data
 */
interface FlightData {
    @GET("flights/{ident}")
    fun getPlane(
        @Path("ident") ident : String,
        @Header("x-apikey") xapikey: String
    ): Call<FlightAware>
}