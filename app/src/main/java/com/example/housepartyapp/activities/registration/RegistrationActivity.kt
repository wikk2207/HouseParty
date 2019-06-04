package com.example.housepartyapp.activities.registration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.LoginActivity
import com.example.housepartyapp.api.registration.RegistrationAPI
import com.example.housepartyapp.api.registration.RegistrationCallBack
import com.example.housepartyapp.api.registration.RegistrationData
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationActivity : AppCompatActivity() {
    lateinit var firstNameEditText: EditText
    lateinit var lastNameEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var repeatPasswordEditText: EditText
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)

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

    fun goToLoginActivity(view: View) {
        val myIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(myIntent, 123)
    }

    fun register(view: View) {
        val firstname = firstNameEditText.text.toString()
        val lastname = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val repeatPassword = repeatPasswordEditText.text.toString()


        if (firstname == ""
            || lastname == ""
            || email == ""
            || password == ""
            || repeatPassword == ""
        ) {
            showAlertDialog(R.string.warningAlert, R.string.emptyFields)
        } else if (password != repeatPassword) {
            showAlertDialog(R.string.warningAlert, R.string.differentPasswords)
        } else {
            registerApi(email, password, "$firstname $lastname")
        }
    }

    fun showAlertDialog(title: Int, message: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNeutralButton("Ok") { dialog, which ->

        }
        builder.show()
    }

    private fun registerApi(email: String, password: String, fullName: String) {
        val registrationData = RegistrationData(email, password, fullName)
        val registration = retrofit.create(RegistrationAPI::class.java)
        val call = registration.signUp(registrationData)
        val context = this
        call.enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("registration2019", "ups")
                showAlertDialog(R.string.warningAlert, R.string.noInternetConnection)
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val body = response.errorBody()
                Log.d("registration2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    val myIntent = Intent(context, LoginActivity::class.java)
                    startActivityForResult(myIntent, 123)
                } else if (response.code() == 400) {
                    if (body != null) {
                        val gson = Gson()
                        val registrationCallBack: RegistrationCallBack =
                            gson.fromJson(body.string(), RegistrationCallBack::class.java)
                        if (registrationCallBack.message == "unique") {
                            Log.d("registration2019", "unique")
                            showAlertDialog(R.string.warningAlert, R.string.invalidEmail)
                        } else if (registrationCallBack.message == "validation") {
                            Log.d("registration2019", "validation")
                            showAlertDialog(R.string.warningAlert, R.string.invalidData)
                        }
                    }
                }
            }
        })
    }
}
