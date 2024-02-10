package dev.prince.prodspec.network

import dev.prince.prodspec.data.Product
import retrofit2.http.GET

interface ApiService {

    @GET("public/get")
    suspend fun getProducts(): List<Product>

}