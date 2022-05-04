package com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Data


/**
 * FlightFocus
 * Is a JSON response class
 * used for getting the flight(s) corresponding to flight code :
 * @seeExamples : SWA1065, SWA1259, UAL123, AA123, UPS204, etc...
 */
data class flights(var ident: String = "",
                   var fa_flight_id : String = "",
                   var operator : String= "",
                   var operator_iata: String = "",
                   var cancelled : Boolean = false,
                   var origin : Origin,
                   var destination: Destination,
                   var gate_origin : String,
                   var gate_destination : String,
                   var scheduled_out : String)

data class Origin(var code : String = "")
data class Destination(var code : String = "")

class FlightResponse : ArrayList<flights>() // Really not sure why, but this works!
class FlightAware {
    val flights: FlightResponse? = null
}