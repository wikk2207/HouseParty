package com.example.housepartyapp.api.dish


import retrofit2.Call
import retrofit2.http.*

interface DishApi {
    @GET("api/dishes/{id}")
    fun calculate(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Path("id") id: String
    ): Call<DishCallBack>

    @POST("api/dish")
    fun addDish(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Body body: NewDishData): Call<NewDishCallBack>
}