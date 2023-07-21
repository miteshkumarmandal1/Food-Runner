package com.mitesh.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mitesh.foodrunner.FoodDetail
import com.mitesh.foodrunner.R
import com.mitesh.foodrunner.activity.ResMenu
import com.mitesh.foodrunner.database.RestaurantDatabase
import com.mitesh.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter(val context: Context, val foodList: ArrayList<FoodDetail>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recycler_row:LinearLayout=view.findViewById(R.id.recycler_row)
        val recyclerview_row_name: TextView = view.findViewById(R.id.recylerview_row_name)
        val recyclerview_row_price: TextView = view.findViewById(R.id.recylerview_row_price)
        val recyclerview_row_rating: TextView = view.findViewById(R.id.recylerview_row_rating)
        val recyclerview_row_image: ImageView = view.findViewById(R.id.recylerview_row_image)
        val recyclerview_row_fav: ImageView = view.findViewById(R.id.recylerview_row_fav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_row_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food_obj = foodList[position]
        holder.recyclerview_row_name.text = food_obj.name
        holder.recyclerview_row_price.text = food_obj.cost_for_one
        holder.recyclerview_row_rating.text = food_obj.rating
        holder.recycler_row.setOnClickListener{
            val itemListIntent=Intent(context,ResMenu::class.java)
            itemListIntent.putExtra("res_id",food_obj.id)
            itemListIntent.putExtra("res_name",food_obj.name)
            context.startActivity(itemListIntent)
        }

        val resEntity = RestaurantEntity(
            foodList[position].id,
            foodList[position].name,
            foodList[position].rating,
            foodList[position].cost_for_one,
            foodList[position].image_url
        )

        holder.recyclerview_row_fav.setOnClickListener {

            if (foodList[position].is_fav) {
                holder.recyclerview_row_fav.setImageResource(R.drawable.recyclerview_row_notfav)
                foodList[position].is_fav = false
                DBAsyncTask(context, resEntity, 3).execute()
            } else {
                holder.recyclerview_row_fav.setImageResource(R.drawable.ic_fav)
                foodList[position].is_fav = true
                DBAsyncTask(context, resEntity, 2).execute()
            }
        }
        Picasso.get().load(food_obj.image_url).error(R.drawable.default_food)
            .into(holder.recyclerview_row_image)

        val checkFav = DBAsyncTask(context, resEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.recyclerview_row_fav.setImageResource(R.drawable.ic_fav)
            foodList[position].is_fav = true
        } else {
            holder.recyclerview_row_fav.setImageResource(R.drawable.recyclerview_row_notfav)
            foodList[position].is_fav = false
        }

    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        // Mode 1 to check book is fav or not
        //Mode 2 add to fav
        //Mode3 remove from fav

        val myResDb =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "Restaurant_Database")
                .build()

        @Deprecated("Deprecated do in background")
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val res: RestaurantEntity? =
                        myResDb.restaurantDao().getResByID(restaurantEntity.id)
                    myResDb.close()
                    return res != null
                }
                2 -> {
                    myResDb.restaurantDao().insertRes(restaurantEntity)
                    myResDb.close()
                }
                3 -> {
                    myResDb.restaurantDao().deleteRes(restaurantEntity)
                    myResDb.close()
                }

            }
            return false
        }
    }
}
