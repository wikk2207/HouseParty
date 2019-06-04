package com.example.housepartyapp.activities.finances

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.api.finance.Receipt

class FinanceListAdapter(context: Context, var data: ArrayList<Receipt>) :
    ArrayAdapter<Receipt>(context, R.layout.item_receipt, data) {

    internal class ViewHolder {
        var receiptNameTextView: TextView? = null
        var othersCostTextView: TextView? = null
        var meatCostTextView: TextView? = null
        var alcoholCostTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_receipt, parent, false)

            val viewHolder = ViewHolder()
            viewHolder.receiptNameTextView = view!!.findViewById(R.id.receiptNameTextView)
            viewHolder.othersCostTextView = view!!.findViewById(R.id.othersCostTextView)
            viewHolder.meatCostTextView = view!!.findViewById(R.id.meatCostTextView)
            viewHolder.alcoholCostTextView = view!!.findViewById(R.id.alcoholCostTextView)

            view.tag = viewHolder
        }

        val holder = view.getTag() as ViewHolder

        holder.receiptNameTextView!!.text = data[position].name
        holder.othersCostTextView!!.text = data[position].other_costs.toString()
        holder.meatCostTextView!!.text = data[position].meat_costs.toString()
        holder.alcoholCostTextView!!.text = data[position].alcohol_costs.toString()


        return view
    }
}