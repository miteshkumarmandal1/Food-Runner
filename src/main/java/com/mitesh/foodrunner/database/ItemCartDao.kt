package com.mitesh.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemCartDao {
    @Insert
    fun insertItem(cartItem:ItemEntity)

    @Delete
    fun deleteItem(cartItem: ItemEntity)

    @Query("SELECT * FROM Item_In_Cart")
    fun getAllItem():List<ItemEntity>

    @Query("SELECT * FROM item_in_cart WHERE id=:itemId")
    fun getItemByID(itemId:String):ItemEntity
}