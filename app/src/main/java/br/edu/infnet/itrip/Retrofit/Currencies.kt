package br.edu.infnet.testalphavantage

import com.google.gson.annotations.SerializedName

data class Currencies (
    @SerializedName("name")
    var name : String
//    @SerializedName("code")
//    var code : String,
//    @SerializedName("symbol")
//    var symbol : String
    )
{}