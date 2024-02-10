package dev.prince.prodspec.network

import dev.prince.prodspec.data.ProductResponse
import retrofit2.http.GET

interface ApiService {

    @GET("get")
    suspend fun getProducts(): ProductResponse

}