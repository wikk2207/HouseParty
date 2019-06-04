package com.example.housepartyapp.activities.finances

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.organizer.OrganizerFragment
import com.example.housepartyapp.api.finance.FinanceApi
import com.example.housepartyapp.api.finance.FinanceCallBack
import com.example.housepartyapp.api.finance.Receipt
import kotlinx.android.synthetic.main.fragment_finances.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FinanceFragment : Fragment() {
    lateinit var receiptsListView: ListView
    lateinit var receiptListAdapter: FinanceListAdapter
    lateinit var receiptList: ArrayList<Receipt>
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    lateinit var loggedUserId: String
    lateinit var alcoholCostTextView: TextView
    lateinit var meatCostTextView: TextView
    lateinit var othersCostTextView: TextView
    var partyId: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        val view = inflater.inflate(R.layout.fragment_finances, container, false)
        receiptsListView = view!!.findViewById(R.id.receiptsListView) as ListView

        alcoholCostTextView = view.findViewById(R.id.alcoholCostTextView)
        meatCostTextView = view.findViewById(R.id.meatCostTextView)
        othersCostTextView = view.findViewById(R.id.othersCostTextView)

        receiptList = arrayListOf()

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://houseparty.dev.bonasoft.pl")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        token = getAuthToken()

        loadData()

        receiptListAdapter = FinanceListAdapter(context!!, receiptList)
        receiptsListView.adapter = receiptListAdapter

        return view
    }

    private fun refreshCostsText(receipt: Receipt) {
        val alcoholArray: List<String> = alcoholCostTextView.text.split(" ")
        val meatArray: List<String> = meatCostTextView.text.split(" ")
        val othersArray: List<String> = othersCostTextView.text.split(" ")
        alcoholCostTextView.text = "${alcoholArray.get(0).toFloat() + receipt.alcohol_costs} zł"
        meatCostTextView.text = "${meatArray.get(0).toFloat() + receipt.meat_costs} zł"
        othersCostTextView.text = "${othersArray.get(0).toFloat() + receipt.other_costs} zł"
    }

    private fun loadData() {

        val financeRetrofit = retrofit.create(FinanceApi::class.java)
        val call = financeRetrofit.getFinances("Bearer $token", "application/json", "XMLHttpRequest", partyId.toString())
        call.enqueue(object : Callback<FinanceCallBack> {

            override fun onResponse(call: Call<FinanceCallBack>, response: Response<FinanceCallBack>) {
                val body = response.body()
                Log.d("finance2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        for (receipt in body.receipts) {
                            receiptList.add(receipt)
                            refreshCostsText(receipt)
                        }
                        Log.d("finance2019", receiptList.toString())
                        receiptListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<FinanceCallBack>, t: Throwable) {
                Log.d("finance2019", "ups")
            }
        })
    }

    fun getAuthToken(): String {
        sharedPreferences = this.getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setMenuVisibility(true)
        super.onActivityCreated(savedInstanceState)
        newReceiptButton.setOnClickListener {createNewReceipt()}
    }
    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putInt("partyId", partyId)
        val fragment = OrganizerFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }

    fun createNewReceipt() {

        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_new_receipt)
        val yesBtn = dialog.findViewById(R.id.confirmReceiptButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelConfirmButton) as Button
        val receiptNameEditText = dialog.findViewById(R.id.newReceiptNameEditText) as EditText
        val receiptMeatEditText = dialog.findViewById(R.id.newReceiptmeatCostEditText) as EditText
        val receiptAlcoholEditText = dialog.findViewById(R.id.newReceiptAlcoholEditText) as EditText
        val receiptOthersEditTextView = dialog.findViewById(R.id.newReceiptOthersEditText) as EditText

        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {

            addReceipt(Receipt(receiptNameEditText.text.toString(),
                receiptMeatEditText.text.toString().toFloat(),
                receiptAlcoholEditText.text.toString().toFloat(),
                receiptOthersEditTextView.text.toString().toFloat(),
                partyId))
            dialog.dismiss() }

        dialog.show()
    }

    fun addReceipt(receipt: Receipt) {
        val financeRetrofit = retrofit.create(FinanceApi::class.java)
        val call = financeRetrofit.storeFinance("Bearer $token", "application/json", "XMLHttpRequest", receipt)
        call.enqueue(object : Callback<Any>{
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("finance2019", "ups")
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.d("finance2019", "code = ${response.code()}")
                if (response.code() == 204) {
                    Log.d("finance2019", "it's ok")
                    receiptList.add(receipt)
                    refreshCostsText(receipt)
                    receiptListAdapter.notifyDataSetChanged()
                }
            }

        })
    }
}