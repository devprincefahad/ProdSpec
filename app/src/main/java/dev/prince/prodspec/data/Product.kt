package dev.prince.prodspec.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "product",indices = [Index(value = ["productName"], unique = true)])
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("price")
    var price: Double,
    @SerializedName("product_name")
    var productName: String,
    @SerializedName("product_type")
    var productType: String,
    @SerializedName("tax")
    var tax: Double
)