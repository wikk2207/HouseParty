package com.example.housepartyapp.activities.arrival

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RadioButton
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.organizer.OrganizerFragment

import kotlinx.android.synthetic.main.fragment_arrival.*
import retrofit2.Retrofit


class ArrivalFragment : Fragment() {

    lateinit var listView: ListView
    lateinit var busListAdapter: ArrivalListAdapter
    lateinit var carListAdapter: ArrivalListAdapter
    lateinit var carList: ArrayList<String>
    lateinit var busList: ArrayList<String>
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    lateinit var loggedUserId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_arrival, container, false)
        listView = view.findViewById(R.id.arrivalListView) as ListView

        //loggedUserId = getUserId()


        carList = arrayListOf()
        busList = arrayListOf()
        //loadData()

        carList.add("Wiksa")
        busList.add("Kuba")


        busListAdapter = ArrivalListAdapter(context!!, busList)
        carListAdapter = ArrivalListAdapter(context!!, carList)

        listView.adapter = carListAdapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        busRadioButton.setOnClickListener{onRadioButtonClicked(it)}
        carRadioButton.setOnClickListener { onRadioButtonClicked(it) }
        carRadioButton.isChecked=true
    }

    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putInt("index", 123)
        //TODO przesyłanie info o konkretnej imprezie
        val fragment = OrganizerFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }

    fun onRadioButtonClicked(view: View) {
        if(view is RadioButton) {
             listView.adapter = when (view) {
                carRadioButton -> carListAdapter
                else -> busListAdapter
            }
        }
    }


}