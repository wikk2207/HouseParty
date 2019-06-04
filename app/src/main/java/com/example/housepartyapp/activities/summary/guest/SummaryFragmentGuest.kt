package com.example.housepartyapp.activities.summary.guest

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.guests.guest.GuestListAsGuestFragment

import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.api.summary.SummaryApi
import com.example.housepartyapp.api.summary.SummaryCallBack
import kotlinx.android.synthetic.main.fragment_summary_guest.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SummaryFragmentGuest : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences

    private var partyId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        println(partyId)
        return inflater.inflate(R.layout.fragment_summary_guest, container, false)
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
        guests.setOnClickListener{ showGuests(it) }
        questions.setOnClickListener{ askQuestions(it)}
    }

    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun showGuests(view: View){
        val fragment = GuestListAsGuestFragment()
        val bundle = Bundle()
        bundle.putInt("partyId", partyId)
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }

    fun askQuestions(view: View){
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        //dialog.setContentView(R.layout.dialog_answer_questions)
       /* val yesBtn = dialog.findViewById(R.id.confirmAnswerButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelAnswerButton) as Button
        val yesCar = dialog.findViewById(R.id.yesCar) as RadioButton
        val noCar = dialog.findViewById(R.id.noCar) as RadioButton
        val yesBus = dialog.findViewById(R.id.yesBus) as RadioButton
        val noBus = dialog.findViewById(R.id.noBus) as RadioButton
        val yesNight = dialog.findViewById(R.id.yesNight) as RadioButton
        val noNight = dialog.findViewById(R.id.noNight) as RadioButton
        val yesMeat = dialog.findViewById(R.id.yesMeat) as RadioButton
        val noMeat = dialog.findViewById(R.id.noMeat) as RadioButton
        val yesAlc = dialog.findViewById(R.id.yesAlc) as RadioButton
        val noAlc = dialog.findViewById(R.id.noAlc) as RadioButton*/

        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            var q1 : String
            var q2 : String
            var q3 : String
            var q4 : String
            var q5 : String

            if(yesCar.isChecked) q1="1"
            else q1 ="0"
            sendAnswer(q1,q2,q3,q4,q5)
            dialog.dismiss() }


        dialog.show()
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
                    println("jestem pusty")
                }

                Log.d("am2019", "it works")
            }

            override fun onFailure(call: Call<SummaryCallBack>, t: Throwable) {
                Log.d("am2019", "ups")
                //showAlertDialog(R.string.getFailureTitle, R.string.getFailureMessage)
            }
        })
    }

    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putInt("index", 123)
        val fragment = GuestListAsGuestFragment()
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }
    fun sendAnswer(q1 : String, q2 : String, q3: String, q4:String, q5:String) {

    }

}