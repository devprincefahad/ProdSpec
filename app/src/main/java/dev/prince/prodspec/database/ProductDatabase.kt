package dev.prince.prodspec.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.prince.prodspec.data.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

}