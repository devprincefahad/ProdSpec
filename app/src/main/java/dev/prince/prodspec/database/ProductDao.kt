package dev.prince.prodspec.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.prince.prodspec.data.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: List<Product>)

    @Query("SELECT * FROM product ORDER BY id ASC")
    fun getProductsFromDB(): Flow<List<Product>>

}