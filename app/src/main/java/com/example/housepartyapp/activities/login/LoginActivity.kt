package com.example.housepartyapp.activities.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.home.HomeActivity
import com.example.housepartyapp.activities.registration.RegistrationActivity
import com.example.housepartyapp.api.login.LoginAPI
import com.example.housepartyapp.api.login.LoginCallBack
import com.example.housepartyapp.api.login.LoginData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val SHARED_PREFERENCES = "sharedPrefs"
const val TOKEN = "TOKEN_AUTH_KEY"
const val USER_ID = "User_Id"

class LoginActivity : AppCompatActivity() {
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

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
    }

    fun goToRegistrationActivity(view: View) {
        val myIntent = Intent(this, RegistrationActivity::class.java)
        startActivityForResult(myIntent, 123)
    }

    // TODO do przeniesienia do innych plików (można pomyśleć jak to zoptymalizować, czy nie dodawać niepotrzebnie kodu)
    fun getAuthToken(): String {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun saveToken(localToken: String) {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN, localToken)
        editor.apply()
    }

    fun saveUserId(localUserId: String) {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID, localUserId)
        editor.apply()
    }

    //TODO ODKOMENTOWAC LOGOWANIE PONIŻEJ ;)
    fun loginApi(view: View) {
        val email: String = emailEditText.text.toString()
        val password: String = passwordEditText.text.toString()
        val loginData = LoginData(email, password)
        val login = retrofit.create(LoginAPI::class.java)
        val call = login.login(loginData)
        val context = this
        call.enqueue(object : Callback<LoginCallBack> {
            override fun onResponse(call: Call<LoginCallBack>, response: Response<LoginCallBack>) {
                val body = response.body()
                Log.d("login2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        saveToken(body.token)
                        saveUserId(body.userId)
                        Log.d("login2019", body.token)
                        val myIntent = Intent(context, HomeActivity::class.java)
                        startActivityForResult(myIntent, 123)
                    }
                } else {
                    saveToken("")
                    saveUserId("")
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.warningAlert)
                    builder.setMessage(R.string.incorrectPassword)
                    builder.setNeutralButton("Ok") { dialog, which ->
                    }
                    builder.show()
                }
            }

            override fun onFailure(call: Call<LoginCallBack>, t: Throwable) {
                Log.d("login2019", "ups")
                saveToken("")
                saveUserId("")
            }
        })
    }
}
