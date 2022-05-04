package com.cbruinsm.tailwindtwo.API.PLANE.DATABASE.Flight

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.cbruinsm.tailwindtwo.API.PLANE.QUERY.Track.FlightResponse


/**
 * @[NEVER] TOUCH THIS FILE
 */
class Converters {
    @TypeConverter
    fun fromGroupTaskMemberList(value: FlightResponse): String {
        val gson = Gson()
        val type = object : TypeToken<FlightResponse>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGroupTaskMemberList(value: String): FlightResponse {
        val gson = Gson()
        val type = object : TypeToken<FlightResponse>() {}.type
        return gson.fromJson(value, type)
    }
}