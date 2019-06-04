package com.example.housepartyapp.activities.new_party

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.login.USER_ID
import com.example.housepartyapp.activities.party.PartyFragment
import com.example.housepartyapp.api.new_party.NewPartyData
import com.example.housepartyapp.api.party.Party
import kotlinx.android.synthetic.main.fragment_new_party1.*
import java.util.*

class NewParty1Fragment : Fragment() {


    private var dayStart = 0
    private var monthStart = 0
    private var yearStart = 0
    private var hourStart = 0
    private var minuteStart = 0
    private var dayEnd = 0
    private var monthEnd = 0
    private var yearEnd = 0
    private var hourEnd = 0
    private var minuteEnd = 0
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    lateinit var loggedUserId: String

    private lateinit var party : NewPartyData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_party1, container, false)
        // Inflate the layout for this fragment
        loggedUserId = getUserId()
        token=getAuthToken()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        goToPart2Button.setOnClickListener { onNextClick() }
        startCalendarButton.setOnClickListener { showDatePicker(true) }
        startClockButton.setOnClickListener { showTimePicker(true) }
        endCalendarButton.setOnClickListener { showDatePicker(false) }
        endClockButton.setOnClickListener { showTimePicker(false) }

        try {
            party = arguments!!.getSerializable("party") as NewPartyData
            nameEditText.setText(party.name)
            addressEditText.setText(party.place)
        } catch (e : NullPointerException) {

        }

    }

    fun onNextClick() {
        var name = nameEditText.text.toString()
        var start ="$yearStart-$monthStart-$dayStart $hourStart:$minuteStart:00"
        var end = "$yearEnd-$monthEnd-$dayEnd $hourEnd:$minuteEnd:00"
        var place = addressEditText.text.toString()
        //TODO zmienić id, costs itd
        party = NewPartyData(name, start, end, place,loggedUserId, "")


        val bundle = Bundle()
        bundle.putSerializable("party",party)
        val fragment = NewParty2Fragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()

    }


    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            PartyFragment()
        )?.commit()
    }

    fun showDatePicker(isStart : Boolean) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, dayOfMonth ->
            if(isStart) {
                dayStart = dayOfMonth
                monthStart = mMonth + 1
                yearStart = mYear
            } else {
                dayEnd = dayOfMonth
                monthEnd = mMonth + 1
                yearEnd = mYear
            }
            setDate(isStart,dayOfMonth, mMonth+1, mYear)
        }, year, month, day)
        dpd.show()
    }

    fun showTimePicker(isStart: Boolean) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            if(isStart){
                hourStart = hourOfDay
                minuteStart = minute
            } else {
                hourEnd = hourOfDay
                minuteStart = minute
            }
            setTime(isStart,hourOfDay, minute)
        },hour,min,true)
        tpd.show()
    }

    fun setDate(isStart: Boolean, day : Int, month : Int, year : Int) {
        var dayString = ""
        var monthString = ""
        if (day < 10) dayString="0$day"
        else dayString=day.toString()

        if(month < 10) monthString="0$month"
        else monthString=month.toString()

        if(isStart) startDateEditText.setText("$dayString-$monthString-$year")
        else endDateEditText.setText("$dayString-$monthString-$year")
    }

    fun setTime(isStart: Boolean, hour : Int, minute : Int) {
        var hourString = ""
        var minuteString = ""
        if (hour < 10) hourString="0$hour"
        else hourString=hour.toString()

        if(minute < 10) minuteString="0$minute"
        else minuteString=minute.toString()

        if(isStart) startTimeEditText.setText("$hourString:$minuteString")
        else endTimeEditText.setText("$hourString:$minuteString")
    }
    fun getAuthToken(): String {
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun getUserId(): String{
        sharedPreferences = getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID, "")
    }

}
