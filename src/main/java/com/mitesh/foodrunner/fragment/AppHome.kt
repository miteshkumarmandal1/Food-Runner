package com.mitesh.foodrunner.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.adapter.HomeRecyclerAdapter
import com.mitesh.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap
import android.content.Context as Context1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppHome : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var recyclerView_home: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progresslayout:RelativeLayout
    lateinit var progressbar:ProgressBar
    var foodInfoList = arrayListOf<FoodDetail>()

    var costComaparator = Comparator<FoodDetail>{food1,food2->
        if(food1.cost_for_one.compareTo(food2.cost_for_one,true)==0){
            food2.rating.compareTo(food1.rating,true)
        }else{
            food1.cost_for_one.compareTo(food2.cost_for_one,true)
        }
       // food1.cost_for_one.compareTo(food2.cost_for_one,true)
    }

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_app_home, container, false)
        setHasOptionsMenu(true)//it do not require in Activity, only require in fragments..
        recyclerView_home = view.findViewById(R.id.recylerView_home)
        progresslayout=view.findViewById(R.id.progress_layout)
        progressbar=view.findViewById(R.id.progress_bar)
        layoutManager = LinearLayoutManager(activity)
        progresslayout.visibility=View.VISIBLE
        val cm = ConnectionManager()
        val isConnected = cm.checkConnectivity(activity as android.content.Context)
        if (isConnected) {

            val requestQueue = Volley.newRequestQueue(activity as Context1)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {

                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = FoodDetail(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )
                                foodInfoList.add(foodObject)

                            }
                            progresslayout.visibility=View.GONE
                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context1, foodInfoList)
                            recyclerView_home.adapter = recyclerAdapter
                            recyclerView_home.layoutManager = layoutManager
                            recyclerView_home.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerView_home.context,
                                    (layoutManager as LinearLayoutManager).orientation
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
            val dialog = AlertDialog.Builder(activity as android.content.Context)
            dialog.setTitle("No Internet Connection")
            dialog.setMessage("What do you want?")
            dialog.setPositiveButton("open setting") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Close App") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
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
         * @return A new instance of fragment AppHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.action_sort){
            Collections.sort(foodInfoList,costComaparator)
            //foodInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}