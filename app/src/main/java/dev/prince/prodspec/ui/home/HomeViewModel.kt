package dev.prince.prodspec.ui.home

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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    init {
        getProductsList()
    }

    private fun getProductsList() {
        viewModelScope.launch {
            try {
                val result = api.getProductsFromApi()
                productDao.insert(result)
                val cachedProducts = productDao.getProductsFromDB()
                _products.emit(Resource.Success(cachedProducts))
            } catch (e: Exception) {
                val cachedProducts = productDao.getProductsFromDB()
                if (e is IOException) {
                    if (cachedProducts.isNotEmpty()) {
                        _products.emit(Resource.Success(cachedProducts))
                    } else {
                        _products.emit(Resource.Error("No internet connection"))
                    }
                } else {
                    _products.emit(Resource.Error(e.message ?: "Something went wrong"))
                }
                e.printStackTrace()
            }
        }
    }
}