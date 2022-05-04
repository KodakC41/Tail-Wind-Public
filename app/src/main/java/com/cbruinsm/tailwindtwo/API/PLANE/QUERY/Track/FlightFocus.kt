package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track

/**
 * FlightFocus
 * Is a JSON response class used for getting the flight path from the API
 * @see fa_flight_id
 */
data class fixes(var name: String = "", var latitude : Double, var longitude : Double, var type : String, var description : String)

class FlightResponse : ArrayList<fixes>()
class FlightFocus {
    var fixes: FlightResponse? = null
}