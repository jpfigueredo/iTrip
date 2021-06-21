package br.edu.infnet.itrip.Model

import java.util.HashMap

class Trip (
    val id: String? = "",
    val countryTrip: String = "",
    val dateTrip: String = "",
    val photoTrip: String = "",
    val descriptionTrip: String = "",
    val ratingTrip: String = ""
) {

    fun toMap(): Map<String,String> {

        val result = HashMap<String,String>()
        result["id"] = id.toString()
        result["countryTrip"] = countryTrip
        result["dateTrip"] = dateTrip
        result["photoTrip"] = photoTrip
        result["descriptionTrip"] = descriptionTrip
        result["ratingTrip"] = ratingTrip

        return result
    }

}