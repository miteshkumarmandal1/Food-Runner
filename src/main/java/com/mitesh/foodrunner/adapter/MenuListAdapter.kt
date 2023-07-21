package com.mitesh.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mitesh.foodrunner.MenuListItem
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.database.ItemCartDatabase
import com.mitesh.foodrunner.database.ItemEntity
import com.mitesh.foodrunner.database.RestaurantDatabase
import com.mitesh.foodrunner.database.RestaurantEntity

class MenuListAdapter(val context: Context, var itemList: ArrayList<MenuListItem>) :
    RecyclerView.Adapter<MenuListAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item_serial: TextView = view.findViewById(R.id.item_serial)
        val item_name: TextView = view.findViewById(R.id.item_name)
        val item_price: TextView = view.findViewById(R.id.item_price)
        val bt_add_cart: Button = view.findViewById(R.id.bt_add_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.menu_list_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemObject = itemList[position]
        holder.item_serial.text = position.toString()
        holder.item_name.text = itemObject.name
        holder.item_price.text = itemObject.cost_for_one

        val itemEntity=ItemEntity(
            itemObject.id,
            itemObject.name,
            itemObject.cost_for_one,
            itemObject.restaurant_id
        )

        holder.bt_add_cart.setOnClickListener {

            if(itemObject.inCart){
                holder.bt_add_cart.text = "ADD"
                val favcolor = ContextCompat.getColor(context.applicationContext,
                    R.color.color_primary
                )
                holder.bt_add_cart.setBackgroundColor(favcolor)
                CartDBAsyncTask(context,itemEntity,3).execute()
                itemObject.inCart=false
            }else{
                holder.bt_add_cart.text = "REMOVE"
                val favcolor = ContextCompat.getColor(context.applicationContext,
                    R.color.teal_700
                )
                holder.bt_add_cart.setBackgroundColor(favcolor)
                CartDBAsyncTask(context,itemEntity,2).execute()
                itemObject.inCart=true
            }
            //holder.bt_add_cart.text = "Remove"
            //val favcolor = ContextCompat.getColor(context.applicationContext, R.color.teal_200)
            //holder.bt_add_cart.setBackgroundColor(favcolor)
        }

        val checkCart = CartDBAsyncTask(context, itemEntity, 1).execute()
        val inCart = checkCart.get()

        if (inCart) {
            holder.bt_add_cart.text = "REMOVE"
            val favcolor = ContextCompat.getColor(context.applicationContext,
                R.color.teal_700
            )
            holder.bt_add_cart.setBackgroundColor(favcolor)
            itemObject.inCart=true
        } else {
            holder.bt_add_cart.text = "ADD"
            val favcolor = ContextCompat.getColor(context.applicationContext,
                R.color.color_primary
            )
            holder.bt_add_cart.setBackgroundColor(favcolor)
            itemObject.inCart=false
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class CartDBAsyncTask(val context: Context, val itemEntity: ItemEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        // Mode 1 to check book is fav or not
        //Mode 2 add to fav
        //Mode3 remove from fav

        val myCartDb =
            Room.databaseBuilder(context, ItemCartDatabase::class.java, "Cart_Database")
                .build()

        @Deprecated("Deprecated do in background")
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val item: ItemEntity? =
                        myCartDb.itemcartDao().getItemByID(itemEntity.id)
                    myCartDb.close()
                    return item != null
                }
                2 -> {
                    myCartDb.itemcartDao().insertItem(itemEntity)
                    myCartDb.close()
                }
                3 -> {
                    myCartDb.itemcartDao().deleteItem(itemEntity)
                    myCartDb.close()
                }

            }
            return false
        }
    }
}