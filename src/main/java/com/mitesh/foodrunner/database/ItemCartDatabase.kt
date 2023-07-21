package com.mitesh.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [ItemEntity::class], version = 1)
abstract class ItemCartDatabase:RoomDatabase() {
    abstract fun itemcartDao():ItemCartDao
}