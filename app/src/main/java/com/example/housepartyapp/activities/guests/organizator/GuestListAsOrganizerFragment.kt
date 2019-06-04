package com.example.housepartyapp.activities.guests.organizator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.guests.guest.GuestsListAsGuestAdapter
import com.example.housepartyapp.activities.login.USER_ID
import com.example.housepartyapp.activities.summary.guest.SummaryFragmentGuest
import com.example.housepartyapp.api.guest.GuestApi
import com.example.housepartyapp.api.guest.GuestCallback
import com.example.housepartyapp.api.guest.Users
import com.example.housepartyapp.api.summary.Guest
import com.example.housepartyapp.api.summary.GuestsListCallBack
import com.example.housepartyapp.api.summary.SummaryApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GuestListAsOrganizerFragment : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var guestListView: ListView
    lateinit var guestListAdapter: GuestListAsOrganizerAdapter
    lateinit var usersList: ArrayList<Users>

    private var partyId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_guests, container, false)
        guestListView = view.findViewById(R.id.guestListView) as ListView
        partyId  = arguments!!.getInt("partyId")
        fulfillGuestList()
        usersList = arrayListOf()

        guestListAdapter =
            GuestListAsOrganizerAdapter(this.context!!, usersList, partyId)
        guestListView.adapter = guestListAdapter

        return view
    }

    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun getUserId(): String{
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID, "")
    }

    fun fulfillGuestList(){
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


        val guest = retrofit.create(GuestApi::class.java)
        val call = guest.getUsersList("Bearer " + getAuthToken(), "application/json", "XMLHttpRequest", getUserId())
        call.enqueue(object : Callback<GuestCallback> {
            override fun onResponse(call: Call<GuestCallback>, response: Response<GuestCallback>) {
                var body: GuestCallback? = response.body()
                if (body != null) {
                    println(body)
                    for (user in body.users) {

                        usersList.add(user)
                    }
                    Log.d("guest", usersList.toString())
                    guestListAdapter.notifyDataSetChanged()
                } else {
                    Log.d("am2019", "ups")
                }
            }

            override fun onFailure(call: Call<GuestCallback>, t: Throwable) {
                Log.d("am2019", "ups")
                showAlertDialog(R.string.getFailureTitle, R.string.getFailureMessage)
            }
        })
    }

    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            SummaryFragmentGuest()
        )?.commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setMenuVisibility(true)
        super.onActivityCreated(savedInstanceState)
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