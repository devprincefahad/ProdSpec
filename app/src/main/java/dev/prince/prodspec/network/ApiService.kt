package dev.prince.prodspec.network

import dev.prince.prodspec.data.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @GET("public/get")
    suspend fun getProductsFromApi(): List<Product>

    @Multipart
    @POST("public/add")
    suspend fun addItem(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part>?
    )

}