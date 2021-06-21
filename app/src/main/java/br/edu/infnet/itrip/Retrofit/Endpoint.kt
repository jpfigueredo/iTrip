package br.edu.infnet.itrip.Retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Endpoint {

    @GET("name/{name}?fullText=true")
    fun getCountry(@Path("name") name: String) : Call<List<Country>>

}