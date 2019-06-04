package com.example.housepartyapp.activities.summary.guest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.housepartyapp.R

import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.organizer.OrganizerFragment
import com.example.housepartyapp.api.summary.SummaryApi
import com.example.housepartyapp.api.summary.SummaryCallBack
import com.example.housepartyapp.api.summary.SummaryData
import kotlinx.android.synthetic.main.fragment_summary_guest.*
import kotlinx.android.synthetic.main.fragment_summary_guest.date
import kotlinx.android.synthetic.main.fragment_summary_guest.description
import kotlinx.android.synthetic.main.fragment_summary_guest.place
import kotlinx.android.synthetic.main.fragment_summary_guest.title
import kotlinx.android.synthetic.main.fragment_summary_organizer.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SummaryFragmentOrganizer : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences

    private var partyId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        println(partyId)
        return inflater.inflate(R.layout.fragment_summary_organizer, container, false)
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
        fulfillSummary()
        saveSummary.setOnClickListener{ saveChanges(it) }
    }

    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun saveChanges(view: View){
        val name = title.text.toString()
        val date = date.text.toString()
        println(date)
        val tmp = date.split(" ")
        val start = tmp[0] + " 16:00:00"
        val end = tmp[2] + " 23:00:00"
        val place = place.text.toString()
        val partyDescription = description.text.toString()
        val profileData = SummaryData(name, start, end, place, partyDescription)
        val profile = retrofit.create(SummaryApi::class.java)
        val call = profile.updateParty("Bearer " + getAuthToken(), "application/json", "XMLHttpRequest", profileData, partyId.toString())
        call.enqueue(object : Callback<SummaryCallBack> {
            override fun onResponse(call: Call<SummaryCallBack>, response: Response<SummaryCallBack>) {
                if (response.code() == 204) {
                    Log.d("am2019", "It works123")
                    showAlertDialog(R.string.successTitle, R.string.successMessage)
                } else {
                    Log.d("am2019", "Something went wrong")
                    showAlertDialog(R.string.failureTitle, R.string.failureMessage)
                }
            }

            override fun onFailure(call: Call<SummaryCallBack>, t: Throwable) {
                Log.d("am2019", "ups")
            }
        })
    }

    fun fulfillSummary(){
        val summary = retrofit.create(SummaryApi::class.java)
        var partyDate = ""
        var dateStart: List<String>
        var dateEnd : List<String>
        val call = summary.setSummaryInfo("Bearer " + getAuthToken(), "application/json", "XMLHttpRequest", partyId.toString())
        call.enqueue(object : Callback<SummaryCallBack> {
            override fun onResponse(call: Call<SummaryCallBack>, response: Response<SummaryCallBack>) {
                var body: SummaryCallBack? = response.body()
                if (body != null) {
                    println(body)
                    title.text = body.name
                    description.text = body.description
                    place.text = body.place
                    dateStart = body.start.split(" ")
                    dateEnd = body.end.split(" ")
                    partyDate = dateStart[0] + " - " + dateEnd[0]
                    date.text = partyDate
                } else {
                    Log.d("am2019", "ups")
                }
            }

            override fun onFailure(call: Call<SummaryCallBack>, t: Throwable) {
                Log.d("am2019", "ups")
                showAlertDialog(R.string.getFailureTitle, R.string.getFailureMessage)
            }
        })
    }

    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putInt("partyId", partyId)
        val fragment = OrganizerFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
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