package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Tracker

/**
 * FlightFocus
 * Is a JSON response class used for getting the flight path from the API
 * @see fa_flight_id
 */
data class positions(var groundspeed : Int, val latitude : Double, val longitude : Double)

class FlightResponse : ArrayList<positions>()
class FlightTracker {
    var positions: FlightResponse? = null
}