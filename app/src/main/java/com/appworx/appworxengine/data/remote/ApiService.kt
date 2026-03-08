package com.appworx.appworxengine.data.remote

import com.appworx.appworxengine.data.model.ProductResponse
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): ProductResponse
}