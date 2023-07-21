package com.mitesh.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.MenuListItem
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.adapter.HomeRecyclerAdapter
import com.mitesh.foodrunner.adapter.MenuListAdapter
import com.mitesh.foodrunner.util.ConnectionManager
import org.json.JSONException

class ResMenu : AppCompatActivity(),View.OnClickListener {
    lateinit var recyclerview_resMenu:RecyclerView
    lateinit var progress_layout_resMenu:RelativeLayout
    lateinit var progress_baar_resMenu:ProgressBar
    lateinit var bt_cart_resMenu:Button
    lateinit var app_toolbar:Toolbar
    lateinit var no_internet:RelativeLayout
    lateinit var layoutManagerResMenu: RecyclerView.LayoutManager
    lateinit var recyclerAdapterResMenu: MenuListAdapter
    var menuItemInfoList= arrayListOf<MenuListItem>()
     var resId:String? = null
    var res_name:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_res_menu)

        if(intent!=null){
            resId=intent.getStringExtra("res_id").toString()
            res_name=intent.getStringExtra("res_name").toString()
        }

        layoutManagerResMenu=LinearLayoutManager(this)
        recyclerview_resMenu=findViewById(R.id.recylerView_resMenu)
        progress_baar_resMenu=findViewById(R.id.progress_bar_resMenu)
        bt_cart_resMenu=findViewById(R.id.bt_cart_resMenu)
        app_toolbar=findViewById(R.id.app_toolbar)
        progress_layout_resMenu=findViewById(R.id.progress_layout_resMenu)
        no_internet=findViewById(R.id.no_internet)
        bt_cart_resMenu.setOnClickListener(this)
        no_internet.visibility=View.GONE
        setUpToolbar()
        progress_layout_resMenu.visibility=View.VISIBLE

        val cm = ConnectionManager()
        val isConnected = cm.checkConnectivity(this@ResMenu)
        if (isConnected) {
             //http://13.235.250.119/v2/restaurants/fetch_result/
            val requestQueue = Volley.newRequestQueue(this@ResMenu)
            var url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            url=url+resId
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {

                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                            println("My res menu : $it")

                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val itemJsonObject = data.getJSONObject(i)
                                val menuItemObject = MenuListItem(
                                    itemJsonObject.getString("id"),
                                    itemJsonObject.getString("name"),
                                    itemJsonObject.getString("cost_for_one"),
                                    itemJsonObject.getString("restaurant_id")
                                )
                                menuItemInfoList.add(menuItemObject)

                            }
                            progress_layout_resMenu.visibility=View.GONE
                            recyclerAdapterResMenu= MenuListAdapter(this@ResMenu, menuItemInfoList)
                            recyclerview_resMenu.adapter = recyclerAdapterResMenu
                            recyclerview_resMenu.layoutManager = layoutManagerResMenu
                            recyclerview_resMenu.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerview_resMenu.context,
                                    (layoutManagerResMenu as LinearLayoutManager).orientation
                                )
                            )

                        } else {
                            println("Not Success Mitesh")
                            Toast.makeText(
                                this@ResMenu,
                                "Error occured in fetching data from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@ResMenu,
                            "Some error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@ResMenu,
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
             no_internet.visibility=View.VISIBLE
            progress_layout_resMenu.visibility=View.GONE
            bt_cart_resMenu.visibility=View.GONE

        }
    }
    fun setUpToolbar() {
        setSupportActionBar(app_toolbar)
        supportActionBar?.title=res_name

    }

    override fun onClick(p0: View?) {
        val intent=Intent(this,MyCart::class.java)
        intent.putExtra("res_name",res_name)
        intent.putExtra("res_id",resId)
        startActivity(intent)
    }
}