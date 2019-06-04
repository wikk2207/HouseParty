package com.example.housepartyapp.api.shop


import retrofit2.Call
import retrofit2.http.*

interface ShopApi {
    @GET("api/shops/{id}")
    fun calculate(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Path("id") id: String
    ): Call<ShopCallBack>

}