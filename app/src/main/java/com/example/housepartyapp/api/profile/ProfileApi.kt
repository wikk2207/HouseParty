package com.example.housepartyapp.api.profile

import retrofit2.Call
import retrofit2.http.*


interface ProfileApi {
    @GET("api/aboutMe/{userId}")
    fun setProfileInfo(@Header("Authorization") authHeader : String,
                       @Header("Content-Type") contentTypeHeader : String,
                       @Header("X-Requested-With") XRequest : String,
                       @Path("userId") userId : String) : Call<ProfileCallBack>

    @PATCH("api/aboutMe/{userId}")
    fun updateProfileInfo(@Header("Authorization") authHeader : String,
                          @Header("Content-Type") contentTypeHeader : String,
                          @Header("X-Requested-With") XRequest : String,
                          @Body body: ProfileData,
                          @Path("userId") userId : String) : Call<ProfileCallBack>
}