package com.mitesh.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitesh.foodrunner.MenuListItem
import com.mitesh.foodrunner.R

class CartAdapter(val context: Context, var cartItemList: ArrayList<MenuListItem>) :
    RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cart_item_name: TextView = view.findViewById(R.id.cart_item_name)
        val cart_item_price: TextView = view.findViewById(R.id.cart_item_price)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_row, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
          holder.cart_item_name.text=cartItemList[position].name
          holder.cart_item_price.text=cartItemList[position].cost_for_one
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }


}