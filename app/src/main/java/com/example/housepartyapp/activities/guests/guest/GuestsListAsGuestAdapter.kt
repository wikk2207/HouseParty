package com.example.housepartyapp.activities.guests.guest


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.api.summary.Guest

class GuestsListAsGuestAdapter(context: Context, var data: ArrayList<Guest>) :
    ArrayAdapter<Guest>(context, R.layout.item_guest_as_guest, data) {

    internal class ViewHolder {
        var userNameTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_guest_as_guest, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.userNameTextView = view!!.findViewById(R.id.userNameTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.userNameTextView!!.text = data[position].full_name
        return view
    }
}