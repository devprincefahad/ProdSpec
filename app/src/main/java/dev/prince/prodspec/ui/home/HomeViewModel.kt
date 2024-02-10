package dev.prince.prodspec.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.database.ProductDao
import dev.prince.prodspec.network.ApiService
import dev.prince.prodspec.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    var showSheet by mutableStateOf(false)

    init {
        getProductsList()
    }

    private fun getProductsList() {
        viewModelScope.launch {
            _products.value = Resource.Loading
            try {
                productDao.getProductsFromDB().collect { products ->
                    if (products.isNotEmpty()) {
                        _products.value = Resource.Success(products)
                    } else {
                        val apiProducts = api.getProductsFromApi()
                        productDao.insert(apiProducts)
                        _products.value = Resource.Success(apiProducts)
                    }
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products")
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            productDao.getProductsFromDB().collect { products ->
                val filteredProducts = products.filter { product ->
                    product.productName.contains(query, ignoreCase = true)
                }
                _products.value = Resource.Success(filteredProducts)
            }
        }
    }

}