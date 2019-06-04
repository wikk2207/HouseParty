package com.example.housepartyapp.api.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST("api/login")
    fun login(@Body body: LoginData): Call<LoginCallBack>
}