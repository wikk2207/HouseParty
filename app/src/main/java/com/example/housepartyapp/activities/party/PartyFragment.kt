package com.example.housepartyapp.activities.party

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
import android.widget.Toast
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.login.USER_ID
import com.example.housepartyapp.activities.new_party.NewParty1Fragment
import com.example.housepartyapp.activities.organizer.OrganizerFragment
import com.example.housepartyapp.activities.summary.guest.SummaryFragmentGuest
import com.example.housepartyapp.api.party.Party
import com.example.housepartyapp.api.party.PartyAPI
import com.example.housepartyapp.api.party.PartyCallBack
import kotlinx.android.synthetic.main.fragment_parties.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PartyFragment : Fragment() {

    lateinit var partyListView: ListView
    lateinit var partyListAdapter: PartyListAdapter
    lateinit var partiesList: ArrayList<Party>
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    lateinit var loggedUserId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_parties, container, false)
        partyListView = view.findViewById(R.id.partyListView) as ListView

        loggedUserId = getUserId()

        partiesList = arrayListOf()

        loadData()

        partyListAdapter = PartyListAdapter(this.context!!, partiesList, loggedUserId)
        partyListView.adapter = partyListAdapter

        partyListView.setOnItemLongClickListener { _, _, index, _ ->
            //our implementation here
            AlertDialog.Builder(this.context!!)
                .setTitle("USUWANIE")
                .setMessage("Czy na pewno chcesz usunąć?")
                .setPositiveButton("TAK") { dialog, which ->
                    partiesList.removeAt(index)
                    partyListAdapter.notifyDataSetChanged()
//                    save()    TODO zapis do bazy o zmianie
                }
                .setNegativeButton("NIE", null)
                .show()

            true
        }
        partyListView.setOnItemClickListener { _, _, index, _ ->

            val bundle = Bundle()
            bundle.putInt("partyId", partiesList[index].id)
            //TODO przesyłanie info o konkretnej imprezie
            val fragment: Fragment
            if (partiesList[index].organizer_id == loggedUserId.toInt()) {
                fragment = OrganizerFragment()
            } else {
                fragment = SummaryFragmentGuest()
            }
            fragment.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                fragment
            )?.commit()
        }

        return view
    }

    private fun loadData() {
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

        token = getAuthToken()

        val partyRetrofit = retrofit.create(PartyAPI::class.java)
        val call = partyRetrofit.getParties("Bearer $token", loggedUserId)
        call.enqueue(object : Callback<PartyCallBack> {

            override fun onResponse(call: Call<PartyCallBack>, response: Response<PartyCallBack>) {
                val body = response.body()
                Log.d("login2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        for (party in body.parties) {
                            partiesList.add(party)
                        }
                        Log.d("login2019", partiesList.toString())
                        partyListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<PartyCallBack>, t: Throwable) {
                Log.d("party2019", "ups")
                showAlertDialog(R.string.warningAlert, R.string.noInternetConnection)
            }
        })
    }

    fun showAlertDialog(title: Int, message: Int) {
        val builder = AlertDialog.Builder(getActivity()!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNeutralButton("Ok") { dialog, which ->

        }
        builder.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setMenuVisibility(true)
        super.onActivityCreated(savedInstanceState)
        createNewPartyButton.setOnClickListener {onClick(it)}
    }

    fun onClick(view: View) {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            NewParty1Fragment()
        )?.commit()
    }
    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun getUserId(): String{
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID, "")
    }

    fun onBackPressed() {
        Toast.makeText(
            this.context,
            "BACK IN PARTY ${activity?.supportFragmentManager?.fragments?.size} ",
            Toast.LENGTH_SHORT
        ).show()
        activity?.supportFragmentManager?.popBackStack();


//        val bundle = Bundle()
//        bundle.putInt("index", 123)
//        //TODO przesyłanie info o konkretnej imprezie
//        val fragment = OrganizerFragment()
//        fragment.arguments = bundle
//        fragmentManager?.beginTransaction()?.replace(
//            R.id.fragment_container,
//            fragment
//        )?.commit()
    }


}