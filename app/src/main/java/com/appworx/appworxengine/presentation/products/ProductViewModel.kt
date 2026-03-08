package com.appworx.appworxengine.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appworx.appworxengine.data.model.Product
import com.appworx.appworxengine.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(
        val products: List<Product>,
        val currentSkip: Int = 0,
        val isPaginating: Boolean = false,
        val isEndOfList: Boolean = false
    ) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val limit = 20
    private var fetchJob: Job? = null

    init {
        fetchInitialProducts()
    }

    private fun fetchInitialProducts() {
        if (fetchJob?.isActive == true) return

        _uiState.value = ProductUiState.Loading

        fetchJob = viewModelScope.launch {
            val result = repository.getProducts(skip = 0, limit = limit)
            result.onSuccess { response ->
                _uiState.value = ProductUiState.Success(
                    products = response.products,
                    currentSkip = 0,
                    isPaginating = false,
                    isEndOfList = response.skip + response.limit >= response.total
                )
            }.onFailure { exception ->
                _uiState.value = ProductUiState.Error(exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun loadNextPage() {
        // Prevent concurrent API calls if a job is already running
        if (fetchJob?.isActive == true) return

        val currentState = _uiState.value as? ProductUiState.Success ?: return
        if (currentState.isEndOfList) return

        val nextSkip = currentState.currentSkip + limit
        _uiState.value = currentState.copy(isPaginating = true)

        fetchJob = viewModelScope.launch {
            val result = repository.getProducts(skip = nextSkip, limit = limit)
            result.onSuccess { response ->
                _uiState.value = ProductUiState.Success(
                    products = currentState.products + response.products,
                    currentSkip = nextSkip,
                    isPaginating = false,
                    isEndOfList = response.skip + response.limit >= response.total
                )
            }.onFailure {
                // On failure, we stop paginating but keep the existing list so the user can try again
                _uiState.value = currentState.copy(isPaginating = false)
            }
        }
    }
}