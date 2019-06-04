package com.example.housepartyapp.activities.dishes

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
import com.example.housepartyapp.R
import com.example.housepartyapp.activities.login.SHARED_PREFERENCES
import com.example.housepartyapp.activities.login.TOKEN
import com.example.housepartyapp.activities.organizer.OrganizerFragment
import com.example.housepartyapp.api.dish.*
import kotlinx.android.synthetic.main.fragment_dishes.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DishesFragment : Fragment() {

    lateinit var dishListView: ListView
    lateinit var dishListAdapter: DishListAdapter
    lateinit var dishes: ArrayList<DishData>
    lateinit var retrofit: Retrofit
    lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    var partyId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        partyId  = arguments!!.getInt("partyId")
        val view = inflater.inflate(R.layout.fragment_dishes, container, false)

        dishListView = view.findViewById(R.id.dishListView) as ListView
        dishes = arrayListOf()
        loadData()
        dishListAdapter = DishListAdapter(this.context!!, dishes)
        dishListView.adapter = dishListAdapter

        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newDishButton.setOnClickListener { newDish() }
    }

    fun loadData() {

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

        val dishRetrofit = retrofit.create(DishApi::class.java)
        val call = dishRetrofit.calculate("Bearer $token", "application/json", "XMLHttpRequest", partyId.toString())
        call.enqueue(object : Callback<DishCallBack> {

            override fun onResponse(call: Call<DishCallBack>, response: Response<DishCallBack>) {
                val body = response.body()
                Log.d("dish2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        for (dish in body.dishes) {
                            dishes.add(dish)
                        }
                        Log.d("dish2019", dishes.toString())
                        dishListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<DishCallBack>, t: Throwable) {
                Log.d("dish2019", "ups")
            }
        })
    }

    fun getAuthToken(): String {
        sharedPreferences = this.getActivity()!!.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "")
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

    fun newDish() {
        var dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_new_dish)
        val yesBtn = dialog.findViewById(R.id.confirmDishButton) as Button
        val noBtn = dialog.findViewById(R.id.cancelDishButton) as Button
        val dishNameEditText = dialog.findViewById(R.id.newDishNameEditText) as EditText
        val dishProductsEditText = dialog.findViewById(R.id.newDishProductsEditText) as EditText


        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesBtn.setOnClickListener {
            addDish(dishNameEditText.text.toString(), dishProductsEditText.text.toString())
            dialog.dismiss() }

        dialog.show()
    }

    fun addDish(name : String, products: String) {
        var dish = NewDishData(name,partyId.toString(), products)
        val dishApi = retrofit.create(DishApi::class.java)
        val call = dishApi.addDish("Bearer $token", "application/json", "XMLHttpRequest",dish)
        call.enqueue(object : Callback<NewDishCallBack> {
            override fun onResponse(call: Call<NewDishCallBack>, response: Response<NewDishCallBack>) {
                val body = response.body()
                Log.d("login2019", "code = ${response.code()}")
                if (response.code() == 200) {
                    if (body != null) {
                        dishes.add(DishData("", partyId.toString(),name, products))
                        dishListAdapter.notifyDataSetChanged()
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<NewDishCallBack>, t: Throwable) {
                Log.d("login2019", "ups")
            }
        })
    }
}