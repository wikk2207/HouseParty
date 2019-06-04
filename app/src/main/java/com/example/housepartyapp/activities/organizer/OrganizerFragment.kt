package com.example.housepartyapp.activities.organizer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.arrival.ArrivalFragment
import com.example.housepartyapp.activities.dishes.DishesFragment
import com.example.housepartyapp.activities.finances.FinanceFragment
import com.example.housepartyapp.activities.guests.organizator.GuestListAsOrganizerFragment
import com.example.housepartyapp.activities.party.PartyFragment

import com.example.housepartyapp.activities.shopping.ShoppingFragment
import com.example.housepartyapp.activities.summary.guest.SummaryFragmentOrganizer
import kotlinx.android.synthetic.main.fragment_organizer.*


class OrganizerFragment : Fragment() {

    private var partyId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        setMenuVisibility(true)
        return inflater.inflate(R.layout.fragment_organizer, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setMenuVisibility(true)
        super.onActivityCreated(savedInstanceState)

        overviwButton.setOnClickListener { onClick(it) }
        guestButton.setOnClickListener { onClick(it) }
        foodButton.setOnClickListener { onClick(it) }
        shopButton.setOnClickListener { onClick(it) }
        carButton.setOnClickListener { onClick(it) }
        cashButton.setOnClickListener { onClick(it) }
    }

    fun onClick(view: View) {

        var fragment = when (view) {
            overviwButton -> SummaryFragmentOrganizer()
            guestButton -> GuestListAsOrganizerFragment()
            foodButton -> DishesFragment()
            shopButton -> ShoppingFragment()
            carButton -> ArrivalFragment()
            else -> FinanceFragment()
        }

        val bundle = Bundle()
        bundle.putInt("partyId", partyId)

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


}