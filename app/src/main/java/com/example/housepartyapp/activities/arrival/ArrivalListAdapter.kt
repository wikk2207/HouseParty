package com.example.housepartyapp.activities.arrival

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.housepartyapp.R

class ArrivalListAdapter(context: Context, var data: ArrayList<String>) :
    ArrayAdapter<String>(context, R.layout.item_party, data) {

    internal class ViewHolder {
        var guestNameTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_guest, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.guestNameTextView = view!!.findViewById(R.id.guestNameTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.guestNameTextView!!.text = data[position]

        return view
    }
}