package com.example.housepartyapp.activities.guests.organizator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.api.guest.Users




class GuestListAsOrganizerAdapter(context: Context, var data: ArrayList<Users>, var partyId: Int) :
    ArrayAdapter<Users>(context, R.layout.item_guest_as_organizer, data) {

    internal class ViewHolder {
        var userNameTextView: TextView? = null
        var addUserButton:ImageButton? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_guest_as_organizer, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.userNameTextView = view!!.findViewById(R.id.userNameTextView)
            viewHolder.addUserButton = view.findViewById(R.id.addUser)

            view.tag = viewHolder
        }



        val holder = view.getTag() as ViewHolder

        holder.userNameTextView!!.text = data[position].full_name
        holder.addUserButton?.setOnClickListener(View.OnClickListener {
            //TODO przesyłanie do API partyId - id imprezy, data[position].id - id użytkownka
            holder.addUserButton!!.setVisibility(View.GONE)
        })
        return view
    }
}