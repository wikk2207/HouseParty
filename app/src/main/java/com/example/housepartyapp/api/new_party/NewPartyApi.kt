package com.example.housepartyapp.api.new_party

import com.example.housepartyapp.api.login.LoginCallBack
import com.example.housepartyapp.api.party.Party
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NewPartyApi {

    @POST("api/party")
    fun addParty(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Body body: NewPartyData): Call<NewPartyCallBack>

    @POST("api/question/create")
    fun addQuestions(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentTypeHeader: String,
        @Header("X-Requested-With") XRequest: String,
        @Body body: NewQuestionsData): Call<QuestionsCallBack>

}