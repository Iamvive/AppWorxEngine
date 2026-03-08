package com.appworx.appworxengine.data.repository

import com.appworx.appworxengine.data.model.Product
import com.appworx.appworxengine.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getProducts(): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts()
                Result.success(response.products)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}