package com.mitesh.foodrunner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Item_In_Cart")
data class ItemEntity(
    @PrimaryKey
    val id :String,
    val name:String,
    val cost_for_one:String,
    val restaurant_id:String
)
