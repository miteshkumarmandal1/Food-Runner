package com.mitesh.foodrunner

class FoodDetail(
    val id: String,
    val name: String,
    val rating: String,
    val cost_for_one: String,
    val image_url: String,
    var is_fav:Boolean=false
) {
}