package com.example.housepartyapp.activities.shopping

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.organizer.OrganizerFragment

import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.api.shop.ShopApi
import com.example.housepartyapp.api.shop.ShopCallBack
import com.example.housepartyapp.api.shop.ShopListAdapter
import kotlinx.android.synthetic.main.shop_item.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ShoppingFragment : Fragment() {


    lateinit var shopListView: ListView
    lateinit var ShopListAdapter: ShopListAdapter
    lateinit var items: ArrayList<ShopData>
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    var partyId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        val view = inflater.inflate(R.layout.fragment_shopping, container, false)

        shopListView = view.findViewById(R.id.shopListView) as ListView
        items = arrayListOf()
        loadData()
        ShopListAdapter = ShopListAdapter(this.context!!, items)
        shopListView.adapter = ShopListAdapter

        return view
    }

    private fun loadData() {
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

        val dishRetrofit = retrofit.create(ShopApi::class.java)
        val call = dishRetrofit.calculate("Bearer $token", "application/json", "XMLHttpRequest", partyId.toString())
        call.enqueue(object : Callback<ShopCallBack> {

            override fun onResponse(call: Call<ShopCallBack>, response: Response<ShopCallBack>) {
                val body = response.body()
                Log.d("shop2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        for (item in body.items) {
                            items.add(item)
                            if(item.already_bought === "1"){
                                checkBox.isChecked = true;
                            }
                        }
                        Log.d("shop2019", items.toString())
                        ShopListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ShopCallBack>, t: Throwable) {
                Log.d("shop2019", "ups")
            }
        })
    }

    fun getAuthToken(): String {
        sharedPreferences = this.getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
    }

    fun onBackPressed() {
        val bundle = Bundle()
        bundle.putInt("index", 123)
        //TODO przesyłanie info o konkretnej imprezie
        val fragment = OrganizerFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            fragment
        )?.commit()
    }

}