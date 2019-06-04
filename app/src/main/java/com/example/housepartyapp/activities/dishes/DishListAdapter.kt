package com.example.housepartyapp.activities.dishes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.api.dish.DishData

class DishListAdapter(context: Context, var data: List<DishData>) :
    ArrayAdapter<DishData>(context, R.layout.item_dish, data) {

    internal class ViewHolder {
        var dishNameTextView: TextView? = null
        var dishStartTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_dish, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.dishNameTextView = view!!.findViewById(R.id.distNameTextView)
            viewHolder.dishStartTextView = view!!.findViewById(R.id.dishProductTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.dishNameTextView!!.text = data[position].name

        holder.dishStartTextView!!.text = data[position].products
        return view
    }
}