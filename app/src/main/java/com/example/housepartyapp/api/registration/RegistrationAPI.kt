package com.example.housepartyapp.api.registration

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {
    @POST("api/register")
    fun signUp(@Body body: RegistrationData): Call<Any>
}