package com.mitesh.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.adapter.HomeRecyclerAdapter
import com.mitesh.foodrunner.adapter.OrderHistoryAdapter
import com.mitesh.foodrunner.util.AllOrderItem
import com.mitesh.foodrunner.util.ConnectionManager
import com.mitesh.foodrunner.util.FoodOrderItem
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerView_history: RecyclerView
    lateinit var layoutManager_history: RecyclerView.LayoutManager
    lateinit var recyclerAdapter_history: OrderHistoryAdapter
    var orderInfoList= arrayListOf<AllOrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences=requireActivity().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
         recyclerView_history=view.findViewById(R.id.recycler_history_fragment)
        layoutManager_history=LinearLayoutManager(activity as Context)
        val cm = ConnectionManager()
        val isConnected = cm.checkConnectivity(activity as android.content.Context)
        if (isConnected) {

            val requestQueue = Volley.newRequestQueue(activity as Context)
            var url = "http://13.235.250.119/v2/orders/fetch_result/"
            url += sharedPreferences.getString("user_id", "100")
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {

                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                             println("My order history:$it")
                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val OrderJsonObject = data.getJSONObject(i)
                                val foodDataArray=OrderJsonObject.getJSONArray("food_items")
                                val orderItemList= arrayListOf<FoodOrderItem>()
                                for(n in 0 until foodDataArray.length()){
                                    val foodDataObject=foodDataArray.getJSONObject(n)
                                    val foodItem=FoodOrderItem(
                                        foodDataObject.getString("food_item_id"),
                                        foodDataObject.getString("name"),
                                        foodDataObject.getString("cost")
                                    )
                                    orderItemList.add(foodItem)
                                }
                                val orderItem= AllOrderItem(
                                    OrderJsonObject.getString("order_id"),
                                    OrderJsonObject.getString("restaurant_name"),
                                    OrderJsonObject.getString("total_cost"),
                                    OrderJsonObject.getString("order_placed_at"),
                                    orderItemList
                                )
                                orderInfoList.add(orderItem)

                            }
                            recyclerAdapter_history =
                                OrderHistoryAdapter(activity as Context,orderInfoList)
                            recyclerView_history.adapter = recyclerAdapter_history
                            recyclerView_history.itemAnimator=DefaultItemAnimator()
                            recyclerView_history.layoutManager = layoutManager_history
                            recyclerView_history.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerView_history.context,
                                    (layoutManager_history as LinearLayoutManager).orientation
                                )
                            )

                        } else {
                            println("Not Success Mitesh")
                            Toast.makeText(
                                activity,
                                "Error occured in fetching data from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        println("Some error occured $e")
                        Toast.makeText(
                            activity as android.content.Context,
                            "Some error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as android.content.Context,
                        "Volley error occured",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val myHeaders = HashMap<String, String>()
                        myHeaders["Content-type"] = "application/json"
                        myHeaders["token"] = "1534961f5f0009"
                        return myHeaders
                    }
                }
            requestQueue.add(jsonObjectRequest)
        } else {
               Toast.makeText(activity as Context,"Please connect to Internet",Toast.LENGTH_SHORT)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}