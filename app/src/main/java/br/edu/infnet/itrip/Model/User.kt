package br.edu.infnet.itrip.Model

import java.util.HashMap

class User (
    val id: String = "",
    val name: String = "",
    val email: String = ""
        ) {

    fun toMap(): Map<String,String> {

        val result = HashMap<String,String>()
        result["id"] = id
        result["name"] = name
        result["email"] = email

        return result
    }
}