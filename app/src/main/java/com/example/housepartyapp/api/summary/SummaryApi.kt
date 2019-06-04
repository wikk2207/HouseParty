package com.example.housepartyapp.api.summary

import retrofit2.Call
import retrofit2.http.*

interface SummaryApi {
    @GET("api/party/{partyId}")
    fun setSummaryInfo(@Header("Authorization") authHeader : String,
                       @Header("Content-Type") contentTypeHeader : String,
                       @Header("X-Requested-With") XRequest : String,
                       @Path("partyId") partyId : String) : Call<SummaryCallBack>

    @GET("api/party/users/{partyId}")
    fun getGuestList(@Header("Authorization") authHeader : String,
                       @Header("Content-Type") contentTypeHeader : String,
                       @Header("X-Requested-With") XRequest : String,
                       @Path("partyId") partyId : String) : Call<GuestsListCallBack>

    @PATCH("api/party/{partyId}")
    fun updateParty(@Header("Authorization") authHeader : String,
                          @Header("Content-Type") contentTypeHeader : String,
                          @Header("X-Requested-With") XRequest : String,
                          @Body body: SummaryData,
                          @Path("partyId") partyId : String) : Call<SummaryCallBack>
}