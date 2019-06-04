package com.example.housepartyapp.api.party

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PartyAPI {
    @GET("api/parties/{id}")
    fun getParties(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Header("X-Requested-With") XRequest: String = "XMLHttpRequest",
        @Header("Content-Type") contentTypeHeader: String = "application/json"
        ): Call<PartyCallBack>
}