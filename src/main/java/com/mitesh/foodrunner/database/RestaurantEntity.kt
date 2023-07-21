package com.mitesh.foodrunner.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurant_Details")
data class RestaurantEntity(
    @PrimaryKey val id:String,
    val name:String,
    val rating:String,
    val cost_for_one:String,
    val image_url:String
)