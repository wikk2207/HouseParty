package com.example.housepartyapp.api.guest

import com.example.housepartyapp.api.summary.SummaryCallBack
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GuestApi {
    @GET("api/aboutMe/userList/{userId}")
    fun getUsersList(@Header("Authorization") authHeader : String,
                       @Header("Content-Type") contentTypeHeader : String,
                       @Header("X-Requested-With") XRequest : String,
                       @Path("userId") userId : String) : Call<GuestCallback>
}