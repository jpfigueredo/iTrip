package br.edu.infnet.itrip.Retrofit

import br.edu.infnet.testalphavantage.Currencies
import br.edu.infnet.testalphavantage.Languages
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name")
    var pais : String,
    @SerializedName("capital")
    var capital : String,
    @SerializedName("subregion")
    var subregion : String,
    @SerializedName("population")
    var population : String,
    @SerializedName("area")
    var area : String,
    @SerializedName("flag")
    var flag : String,
    @SerializedName("currencies")
    var currencies : List<Currencies>,
    @SerializedName("languages")
    var languages : List<Languages>
)