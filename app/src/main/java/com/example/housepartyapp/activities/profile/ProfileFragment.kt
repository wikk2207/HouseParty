package com.example.housepartyapp.activities.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.party.PartyFragment
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.login.USER_ID
import com.example.housepartyapp.api.profile.ProfileApi
import com.example.housepartyapp.api.profile.ProfileCallBack
import com.example.housepartyapp.api.profile.ProfileData
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProfileFragment : Fragment() {
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            PartyFragment()
        )?.commit()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()


        retrofit = Retrofit.Builder()
            .baseUrl("http://houseparty.dev.bonasoft.pl")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        super.onActivityCreated(savedInstanceState)
        fulfillInfoAbout()
        saveProfileChanges.setOnClickListener{ saveChanges(it) }
    }

    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun getUserId(): String{
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID, "")
    }

    fun saveChanges(view: View){
        val name = firstName.text.toString()
        val lastName = lastName.text.toString()
        val allergies = allergies.text.toString()
        val fullname = "$name $lastName"
        val userFoodPreferences = foodPreferences.text.toString()
        val userAlcoholPreferences = alcoholPreferences.text.toString()
        val profileData = ProfileData(fullname, allergies,userFoodPreferences, userAlcoholPreferences )
        val profile = retrofit.create(ProfileApi::class.java)
        val call = profile.updateProfileInfo("Bearer " + getAuthToken(), "application/json", "XMLHttpRequest", profileData, getUserId())
        call.enqueue(object : Callback<ProfileCallBack> {
            override fun onResponse(call: Call<ProfileCallBack>, response: Response<ProfileCallBack>) {
                if (response.code() == 200) {
                    Log.d("am2019", "It works")
                    showAlertDialog(R.string.successTitle, R.string.successMessage)
                } else {
                    Log.d("am2019", "Something went wrong")
                    showAlertDialog(R.string.failureTitle, R.string.failureMessage)
                }
            }

            override fun onFailure(call: Call<ProfileCallBack>, t: Throwable) {
                Log.d("am2019", "ups")
            }
        })

    }

    fun fulfillInfoAbout(){
        var fullname = ""
        var temp : List<String>
        val profile = retrofit.create(ProfileApi::class.java)
        val call = profile.setProfileInfo("Bearer " + getAuthToken(), "application/json", "XMLHttpRequest", getUserId())
        call.enqueue(object : Callback<ProfileCallBack> {
            override fun onResponse(call: Call<ProfileCallBack>, response: Response<ProfileCallBack>) {
                var body = response.body()
                fullname = body!!.full_name
                temp = fullname.split(" ")
                if(temp.size > 1 ){
                    lastName.text = Editable.Factory.getInstance().newEditable(temp[1])
                }else{
                    lastName.text = Editable.Factory.getInstance().newEditable("")
                }
                firstName.text = Editable.Factory.getInstance().newEditable(temp[0])
                allergies.text = Editable.Factory.getInstance().newEditable(body.allergies)
                foodPreferences.text = Editable.Factory.getInstance().newEditable(body.food_preferences)
                alcoholPreferences.text = Editable.Factory.getInstance().newEditable(body.alcohol_preferences)

                Log.d("am2019", "it works")
            }

            override fun onFailure(call: Call<ProfileCallBack>, t: Throwable) {
                Log.d("am2019", "ups")
                showAlertDialog(R.string.getFailureTitle, R.string.getFailureMessage)
            }
        })
    }

    fun showAlertDialog(title: Int, message: Int) {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle(title)
        builder?.setMessage(message)
        builder?.setNeutralButton("Ok") { dialog, which ->
            onBackPressed()
        }
        builder?.show()
    }
}
