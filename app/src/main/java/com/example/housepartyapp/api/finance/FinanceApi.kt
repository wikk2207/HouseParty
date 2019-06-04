package com.example.housepartyapp.api.finance

import retrofit2.Call
import retrofit2.http.*

interface FinanceApi {
    @GET("api/receipt/{id}")
    fun getFinances(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Path("id") id: String
    ): Call<FinanceCallBack>

    @POST("api/receipt")
    fun storeFinance(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Body body: Receipt
    ): Call<Any>
}