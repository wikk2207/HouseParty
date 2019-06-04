package com.example.housepartyapp.api.shop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.shopping.ShopData
import com.example.housepartyapp.api.dish.DishData

class ShopListAdapter(context: Context, var data: List<ShopData>) :
    ArrayAdapter<ShopData>(context, R.layout.shop_item, data) {

    internal class ViewHolder {
        var itemNameTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.shop_item, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.itemNameTextView= view!!.findViewById(R.id.NameTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.itemNameTextView!!.text = data[position].product_name

        return view
    }
}