package dev.prince.prodspec.data

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("image")
    var image: String,
    @SerializedName("price")
    var price: Int,
    @SerializedName("product_name")
    var productName: String,
    @SerializedName("product_type")
    var productType: String,
    @SerializedName("tax")
    var tax: Int
)