package com.example.housepartyapp.activities.party

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.api.party.Party

class PartyListAdapter(context: Context, var data: ArrayList<Party>, val id: String) :
    ArrayAdapter<Party>(context, R.layout.item_party, data) {

    internal class ViewHolder {
        var partyNameTextView: TextView? = null
        var partyStartTextView: TextView? = null
        var partyEndTextView: TextView? = null
        var userTypeImageView: ImageView? = null
        var userTypeTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_party, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.partyNameTextView = view!!.findViewById(R.id.partyNameTextView)
            viewHolder.partyStartTextView = view!!.findViewById(R.id.partyStartTextView)
            viewHolder.partyEndTextView = view!!.findViewById(R.id.partyEndTextView)
            viewHolder.userTypeImageView = view!!.findViewById(R.id.userTypeImageView)
            viewHolder.userTypeTextView = view!!.findViewById(R.id.userTypeTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.partyNameTextView!!.text = data[position].name
        holder.partyStartTextView!!.text = data[position].start
        holder.partyEndTextView!!.text = data[position].end

        if (data[position].organizer_id == id.toInt()) {
            holder.userTypeImageView!!.setImageResource(R.drawable.users_group)
            holder.userTypeTextView!!.setText(R.string.organizer)
        } else {
            holder.userTypeTextView!!.setText(R.string.guest)
            holder.userTypeImageView!!.setImageResource(R.drawable.man_user)
        }
        return view
    }
}