package com.mitesh.foodrunner

data class MenuListItem(
    val id: String,
    val name: String,
    val cost_for_one: String,
    val restaurant_id: String,
    var inCart: Boolean = false
)
