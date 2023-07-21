package com.mitesh.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.MenuListItem
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.adapter.CartAdapter
import com.mitesh.foodrunner.adapter.HomeRecyclerAdapter
import com.mitesh.foodrunner.database.ItemCartDatabase
import com.mitesh.foodrunner.database.ItemEntity
import com.mitesh.foodrunner.database.RestaurantDatabase
import com.mitesh.foodrunner.database.RestaurantEntity
import com.mitesh.foodrunner.fragment.AppFavourite
import com.mitesh.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MyCart : AppCompatActivity() ,View.OnClickListener {
    var cartItemList= arrayListOf<MenuListItem>()
    var cartList= listOf<ItemEntity>()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerView_cart: RecyclerView
    lateinit var order_res_name_tv:TextView
    lateinit var success_button:Button
    lateinit var layoutManager_cart: RecyclerView.LayoutManager
    lateinit var recyclerAdapter_cart: CartAdapter
    lateinit var progresslayout_cart: RelativeLayout
    lateinit var progressbar_cart: ProgressBar
    lateinit var bt_order:Button
    lateinit var cart_success:RelativeLayout
    lateinit var cart_empty:RelativeLayout
    var resId:String? = null
    var res_name:String?=null
    var total_cost:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        if(intent!=null){
            resId=intent.getStringExtra("res_id").toString()
            res_name=intent.getStringExtra("res_name").toString()
        }
        order_res_name_tv=findViewById(R.id.order_res_name)
        recyclerView_cart=findViewById(R.id.recylerView_cart)
        layoutManager_cart=LinearLayoutManager(this)
        progressbar_cart=findViewById(R.id.progress_bar_cart)
        progresslayout_cart=findViewById(R.id.progress_layout_cart)
        bt_order=findViewById(R.id.bt_order)
        success_button=findViewById(R.id.success_button)
        success_button.setOnClickListener(this)
        cart_success=findViewById(R.id.cart_success)
        bt_order.setOnClickListener(this)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        cart_empty=findViewById(R.id.cart_empty)
        progresslayout_cart.visibility=View.VISIBLE

        cart_success.visibility=View.GONE
        cart_empty.visibility=View.GONE


        cartList= MyCart.CartitemAccess(this,1,ItemEntity("12","default","12","12")).execute().get()
        var cost:Int=0
        if(!cartList.isEmpty()){
            for(i in cartList.indices){
                val item_detail= MenuListItem(
                      cartList[i].id,
                      cartList[i].name,
                      cartList[i].cost_for_one,
                      cartList[i].restaurant_id,
                    true
                )
                val item_cost:Int=cartList[i].cost_for_one.toInt()
                if(Int!=null)
                     cost=cost+item_cost

                cartItemList.add(item_detail)
            }
            println("My cart is: $cartList")
            println("My cartItem is: $cartItemList")
            val btnStr="Place order(Total Rs. "+cost+")"
            bt_order.text=btnStr
            progresslayout_cart.visibility=View.GONE
            total_cost=cost.toString()
            recyclerAdapter_cart= CartAdapter(this@MyCart,cartItemList)
            recyclerView_cart.adapter=recyclerAdapter_cart
            recyclerView_cart.layoutManager=layoutManager_cart

            recyclerView_cart.addItemDecoration(
                DividerItemDecoration(
                    recyclerView_cart.context,
                    (layoutManager_cart as LinearLayoutManager).orientation
                )
            )

            var topstr="Ordering from: "+res_name
            order_res_name_tv.text=topstr

        }else{
            cart_empty.visibility=View.VISIBLE
            bt_order.visibility=View.GONE
            progresslayout_cart.visibility=View.GONE
        }

    }


    class CartitemAccess(val context: Context,val mode:Int,val itemEntity: ItemEntity): AsyncTask<Void, Void, List<ItemEntity>>() {
        val cartDb= Room.databaseBuilder(context, ItemCartDatabase::class.java,"Cart_Database").build()
        override fun doInBackground(vararg p0: Void?): List<ItemEntity> {
           when(mode){
               1->{
                   val list= cartDb.itemcartDao().getAllItem()
                   cartDb.close()
                   return list
               }
               2->{
                   cartDb.itemcartDao().deleteItem(itemEntity)
               }
           }
          return listOf()
        }

    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.bt_order) {
            val cm = ConnectionManager()
            val isConnected = cm.checkConnectivity(this)
            if (isConnected) {
                val request_queue = Volley.newRequestQueue(this@MyCart)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val jsonParams = JSONObject()
                jsonParams.put("user_id", sharedPreferences.getString("user_id", "10137"))
                jsonParams.put("restaurant_id", resId)
                jsonParams.put("total_cost", total_cost)
                val jsonArray = JSONArray()


                for (i in cartItemList.indices) {
                    val jsonfoodObj = JSONObject()
                    jsonfoodObj.put("food_item_id", cartItemList[i].id)
                    jsonArray.put(jsonfoodObj)
                }
                jsonParams.put("food", jsonArray)
                println("My request is: $jsonParams")

                val jsonrequest =
                    object : JsonObjectRequest(
                        Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")
                                if (success) {
                                        cart_success.visibility=View.VISIBLE
                                        bt_order.visibility=View.GONE
                                        for(i in cartList.indices){
                                            CartitemAccess(this,2,cartList[i]).execute()
                                        }
                                } else {
                                    val error_msg = data1.getString("errorMessage")
                                    Toast.makeText(this, error_msg, Toast.LENGTH_LONG).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this,
                                    "Some error occured: $e",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        },
                        Response.ErrorListener {
                            //error listner
                            Toast.makeText(
                                this@MyCart,
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
                request_queue.add(jsonrequest)

            } else {
                Toast.makeText(this, "Please connect to Internet and try again", Toast.LENGTH_SHORT)
                    .show()
            }


        }
        else if(p0?.id==R.id.success_button){
            val intent=Intent(this@MyCart,AppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}