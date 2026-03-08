package com.appworx.appworxengine.data.repository

import com.appworx.appworxengine.data.model.ProductResponse
import com.appworx.appworxengine.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getProducts(skip: Int, limit: Int = 20): Result<ProductResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts(skip, limit)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}