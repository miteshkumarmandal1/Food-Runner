package com.mitesh.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRes(restaurant:RestaurantEntity)

    @Delete
    fun deleteRes(restaurant: RestaurantEntity)

    @Query("SELECT * FROM Restaurant_Details")
    fun getAllRes():List<RestaurantEntity>

    @Query("SELECT * FROM Restaurant_Details WHERE id=:resId")
    fun getResByID(resId:String):RestaurantEntity
}