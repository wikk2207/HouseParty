package com.example.housepartyapp.activities.new_party

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.home.HomeActivity
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.login.USER_ID
import com.example.housepartyapp.activities.party.PartyFragment
import com.example.housepartyapp.api.login.LoginAPI
import com.example.housepartyapp.api.login.LoginCallBack
import com.example.housepartyapp.api.login.LoginData
import com.example.housepartyapp.api.new_party.*
import com.example.housepartyapp.api.party.Party
import kotlinx.android.synthetic.main.fragment_new_party2.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewParty2Fragment : Fragment() {
    private lateinit var party : NewPartyData
    private lateinit var retrofit : Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    lateinit var loggedUserId: String
    var q1= 0
    var q2=0
    var q3 =0
    var q4=0
    var q5 = 0
    private var partyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        party = arguments!!.getSerializable("party") as NewPartyData

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_party2, container, false)
        // Inflate the layout for this fragment
        loggedUserId = getUserId()
        token=getAuthToken()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createButton.setOnClickListener { createNewParty() }
        backToPart1Button.setOnClickListener { onClick(it) }

        //TODO pobieranie pytań
        checkBox.setText("Pytanie 1")
        checkBox2.setText("Pytanie 2")
        checkBox3.setText("Pytanie 3")
        checkBox4.setText("Pytanie 4")
        checkBox5.setText("Pytanie 5")
    }

    fun onClick(view: View) {
        val bundle = Bundle()
        bundle.putSerializable("party",party)
        var fragment = NewParty1Fragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }


    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putSerializable("party",party)
        var fragment = NewParty1Fragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }

    fun createNewParty() {
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_new_party)
        val yesBtn = dialog.findViewById(R.id.confirmButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelButton) as Button
        val partyNameDialogTextView = dialog.findViewById(R.id.partyNameDialogTextView) as TextView
        val placeDialogTextView = dialog.findViewById(R.id.placeDialogTextView) as TextView
        val startDialogTextView = dialog.findViewById(R.id.startDialogTextView) as TextView
        val endDialogTextView = dialog.findViewById(R.id.endDialogTextView) as TextView
        val questionsDialogTextView = dialog.findViewById(R.id.questionsDialogTextView) as TextView
        val descriptionDialogTextView = dialog.findViewById(R.id.descriptionDialogTextView) as TextView

        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            addParty()

            fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                PartyFragment()
            )?.commit()
            dialog.dismiss() }

        partyNameDialogTextView.text=party.name
        placeDialogTextView.text=party.place
        startDialogTextView.text=party.start
        endDialogTextView.text=party.end

        var questionsText = ""
        if(checkBox.isChecked) {
            questionsText = "$questionsText\n" + checkBox.text
            q1 = 1
        }
        if(checkBox2.isChecked) {
            questionsText = "$questionsText\n" + checkBox2.text
            q2 = 2
        }
        if(checkBox3.isChecked) {
            questionsText = "$questionsText\n" + checkBox3.text
            q3 = 3
        }
        if(checkBox4.isChecked) {
            questionsText = "$questionsText\n" + checkBox4.text
            q4 = 4
        }
        if(checkBox5.isChecked) {
            questionsText = "$questionsText\n" + checkBox5.text
             q5 =5
        }

        questionsDialogTextView.text = questionsText
        descriptionDialogTextView.text = descriptionEditText.text.toString()
        dialog.show()
    }

    fun addParty() {
        party.description=descriptionEditText.text.toString()
        val addParty = retrofit.create(NewPartyApi::class.java)
        val call = addParty.addParty("Bearer $token", "application/json", "XMLHttpRequest",party)
        call.enqueue(object : Callback<NewPartyCallBack> {
            override fun onResponse(call: Call<NewPartyCallBack>, response: Response<NewPartyCallBack>) {
                val body = response.body()
                Log.d("login2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        partyId=body.partyId
                        Log.d("addParty2019", body.partyId.toString())
                        addQuestions()
                    }
                } else {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle(R.string.warningAlert)
                    builder.setMessage(R.string.incorrectPassword)
                    builder.setNeutralButton("Ok") { dialog, which ->
                    }
                    builder.show()
                }
            }

            override fun onFailure(call: Call<NewPartyCallBack>, t: Throwable) {
                Log.d("login2019", "ups")
            }
        })

    }

    fun addQuestions() {

        val newQuestionsData = NewQuestionsData(partyId.toString(),q1,q2,q3,q4,q5)
        val addParty = retrofit.create(NewPartyApi::class.java)
        val call = addParty.addQuestions("Bearer $token", "application/json", "XMLHttpRequest",newQuestionsData)
        call.enqueue(object : Callback<QuestionsCallBack> {
            override fun onResponse(call: Call<QuestionsCallBack>, response: Response<QuestionsCallBack>) {
                val body = response.body()
                Log.d("login2019", "code = ${response.code()}")
                if (response.code() == 204) {

                } else {
                }
            }

            override fun onFailure(call: Call<QuestionsCallBack>, t: Throwable) {
                Log.d("login2019", "ups")
            }
        })    }
    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun getUserId(): String{
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID, "")
    }
}
