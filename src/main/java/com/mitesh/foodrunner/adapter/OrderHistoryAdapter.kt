package com.mitesh.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitesh.foodrunner.MenuListItem
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.util.AllOrderItem
import com.mitesh.foodrunner.util.FoodOrderItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(
    val context: Context,
    val allOrderList: ArrayList<AllOrderItem>,
) :
    RecyclerView.Adapter<OrderHistoryAdapter.AllOrderItemViewHolder>() {
    class AllOrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heading_text_name: TextView = view.findViewById(R.id.res_name_history)
        val order_date_history: TextView = view.findViewById(R.id.order_date_history)
        val recycler_item_order: RecyclerView = view.findViewById(R.id.recycler_item_order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.order_history_row, parent, false)
        return AllOrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllOrderItemViewHolder, position: Int) {
        holder.heading_text_name.text = allOrderList[position].restaurant_name
        holder.order_date_history.text = formatDate(allOrderList[position].order_placed_at)
        setUpRecycler(holder.recycler_item_order, allOrderList[position].food_items)

    }

    override fun getItemCount(): Int {
        return allOrderList.size
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }


    private fun setUpRecycler(
        recyclerItemHistory: RecyclerView,
        foodOrderItemList: ArrayList<FoodOrderItem>
    ) {
        var menuItemList = arrayListOf<MenuListItem>()
        for (i in 0 until foodOrderItemList.size) {
            val menuItem = MenuListItem(
                "100",
                foodOrderItemList[i].name,
                foodOrderItemList[i].cost,
                "102"

            )
            menuItemList.add(menuItem)
        }
        val cartItemAdapter = CartAdapter(context, menuItemList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerItemHistory.layoutManager = mLayoutManager
        recyclerItemHistory.itemAnimator = DefaultItemAnimator()
        recyclerItemHistory.adapter = cartItemAdapter
    }
}