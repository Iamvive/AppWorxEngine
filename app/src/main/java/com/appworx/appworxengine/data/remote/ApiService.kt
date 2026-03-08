package com.appworx.appworxengine.data.remote

import com.appworx.appworxengine.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 20
    ): ProductResponse
}